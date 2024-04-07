from datetime import timedelta, datetime
from sqlalchemy.orm import Session, query, joinedload
from sqlalchemy import desc, asc, func, and_, or_, cast, Float
from datetime import datetime

from database import models, schemas


def create_user(db: Session, user: models.User):
    db.add(user)
    db.commit()
    db.refresh(user)
    return user


def haversine_distance(lat1, lon1, lat2, lon2):
    EARTH_RADIUS = 6371000
    dlat = func.radians(lat2 - lat1)
    dlon = func.radians(lon2 - lon1)
    a = func.sin(dlat / 2) * func.sin(dlat / 2) + func.cos(func.radians(lat1)) * func.cos(func.radians(lat2)) * func.sin(dlon / 2) * func.sin(dlon / 2)
    c = 2 * func.atan2(func.sqrt(a), func.sqrt(1 - a))
    return cast(c * EARTH_RADIUS, Float)  # Cast the result to Float data type


def get_near_users(user: models.User, db: Session):
    return db.query(
        models.User.id,
        models.User.nickname,
        models.User.place,
        models.User.latitude,
        models.User.longitude,
        models.User.last_update
    ).filter(and_(
        models.User.id != user.id,
        haversine_distance(models.User.latitude, models.User.longitude, user.latitude, user.longitude) <= 500,
        models.User.visible == True,
        ~models.User.id.in_(
            db.query(models.User.id).join(
                models.Friendship,
                or_(
                    and_(models.Friendship.user1_id == models.User.id, models.Friendship.user2_id == user.id),
                    and_(models.Friendship.user2_id == models.User.id, models.Friendship.user1_id == user.id)
                )
            )
        ),
        ~models.User.id.in_(
            db.query(models.User.id).join(
                models.FriendRequest,
                or_(
                    and_(models.FriendRequest.user_from == models.User.id, models.FriendRequest.user_to == user.id),
                    and_(models.FriendRequest.user_from == user.id, models.FriendRequest.user_to == models.User.id)
                )
            )
        ),
    )).all()


def get_user_by_token(db: Session, token: str):
    return db.query(models.User).filter(models.User.token == token).first()


def get_user_by_id(db: Session, user_id: int):
    return db.query(models.User).filter(models.User.id == user_id).first()


def update_user(db: Session, user: schemas.UserUpdate):
    upd_user = dict()
    if user.nickname is not None:
        upd_user['nickname'] = user.nickname
    if user.place is not None:
        upd_user['place'] = user.place
    if user.latitude is not None and user.longitude is not None:
        upd_user['latitude'] = user.latitude
        upd_user['longitude'] = user.longitude
        upd_user['last_update'] = datetime.now()
    if user.visible is not None:
        upd_user['visible'] = user.visible
    db.query(models.User).filter(models.User.id == user.id).update(upd_user)
    db.commit()


def get_request(db: Session, user_from: int, user_to: int):
    return db.query(models.FriendRequest).filter(
        and_(models.FriendRequest.user_from == user_from, models.FriendRequest.user_to == user_to)
    ).first()


def request_friend(db: Session, user_from: int, user_to: int):
    db.add(models.FriendRequest(user_from=user_from, user_to=user_to))
    db.commit()


def get_friend_requests(db: Session, user_id: int):
    return db.query(
        models.User.id,
        models.User.nickname,
        models.User.place,
        models.User.latitude,
        models.User.longitude,
        models.User.last_update
    ).join(
        models.FriendRequest,
        models.FriendRequest.user_from == models.User.id
    ).filter(models.FriendRequest.user_to == user_id).all()


def delete_friend_request(db: Session, user_from: int, user_to: int) -> None:
    db.query(models.FriendRequest).filter(
        and_(models.FriendRequest.user_from == user_from, models.FriendRequest.user_to == user_to)
    ).delete()
    db.commit()


def is_friend(db: Session, user1_id: int, user2_id: int):
    return db.query(models.Friendship).filter(
        or_(
            and_(models.Friendship.user1_id == user1_id, models.Friendship.user2_id == user2_id),
            and_(models.Friendship.user1_id == user2_id, models.Friendship.user2_id == user1_id)
        )
    ).first()


def get_friends(db: Session, user_id: int):
    return db.query(
        models.User.id,
        models.User.nickname,
        models.User.place,
        models.User.latitude,
        models.User.longitude,
        models.User.last_update
    ).join(
        models.Friendship,
        or_(
            and_(models.Friendship.user1_id == models.User.id, models.Friendship.user2_id == user_id),
            and_(models.Friendship.user2_id == models.User.id, models.Friendship.user1_id == user_id)
        )
    ).all()


def add_friend(db: Session, user1_id: int, user2_id: int):
    db.add(models.Friendship(user1_id=user1_id, user2_id=user2_id))
    db.query(models.FriendRequest).filter(or_(
        and_(models.FriendRequest.user_from == user1_id, models.FriendRequest.user_to == user2_id),
        and_(models.FriendRequest.user_from == user2_id, models.FriendRequest.user_to == user1_id)
    )).delete()
    db.commit()


def delete_friend(db: Session, user1_id: int, user2_id: int):
    db.query(models.Friendship).filter(
        or_(
            and_(models.Friendship.user1_id == user1_id, models.Friendship.user2_id == user2_id),
            and_(models.Friendship.user1_id == user2_id, models.Friendship.user2_id == user1_id)
        )
    ).delete()
    db.commit()
