from datetime import timedelta, datetime
from sqlalchemy.orm import Session, query, joinedload
from sqlalchemy import desc, asc, func, and_, or_
from datetime import datetime

from database import models, schemas


def create_user(db: Session, user: models.User):
    db.add(user)
    db.commit()
    db.refresh(user)
    return user


def get_user(db: Session, user_id: int):
    return db.query(models.User).filter(models.User.id == user_id).first()


def update_user(db: Session, user: schemas.UserUpdate):
    upd_user = dict()
    if user.nickname is not None:
        upd_user['nickname'] = user.nickname
    if user.place is not None:
        upd_user['place'] = user.place
    if user.coords is not None:
        upd_user['coords'] = user.coords
        upd_user['last_update'] = datetime.now()
    if user.visible is not None:
        upd_user['visible'] = user.visible
    db.query(models.User).filter(models.User.id == user.id).update(upd_user)
    db.commit()


def request_friend(db: Session, user_from: int, user_to: int):
    db.add(models.FriendRequest(user_from=user_from, user_to=user_to))
    db.commit()


def get_friend_requests(db: Session, user_id: int):
    return db.query(
        models.User.id,
        models.User.nickname,
        models.User.place,
        models.User.coords,
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


def get_friends(db: Session, user_id: int):
    return db.query(
        models.User.id,
        models.User.nickname,
        models.User.place,
        models.User.coords,
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
