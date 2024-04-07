from fastapi import Request, APIRouter, Depends, HTTPException
from database import crud, schemas, models
from database.loader import get_db
from sqlalchemy.orm import Session
from .dependencies import get_user as get_user_dependency
from math import cos, sqrt, atan2

router = APIRouter(prefix='/requests')


@router.get('/', response_model=list[schemas.User], status_code=200)
def get_friend_requests(user_id: int, db: Session = Depends(get_db)):
    return crud.get_friend_requests(db, user_id)


def haversine_distance(lat1, lon1, lat2, lon2):
    EARTH_RADIUS = 6371000
    dlat = lat2 - lat1
    dlon = lon2 - lon1
    a = (dlat / 2) ** 2 + cos(lat1) * cos(lat2) * (dlon / 2) ** 2
    c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return c * EARTH_RADIUS



@router.post(
    '/',
    status_code=201,
    responses={
        400: {'description': 'You cannot be friends with yourself'},
        403: {'description': 'Forbidden'},
        404: {'description': 'User not found'},
        409: {'description': 'Friend request already sent or already friends'},
    }
)
def request_friend(user_to: int, user: models.User = Depends(get_user_dependency), db: Session = Depends(get_db)):
    if user.id == user_to:
        raise HTTPException(status_code=400, detail='You cannot be friends with yourself')
    user_model_to = crud.get_user_by_id(db, user_to)
    if user_model_to is None:
        raise HTTPException(status_code=404, detail='User not found')
    if not user.visible:  # If the user is invisible, he cannot send friend requests
        raise HTTPException(status_code=403, detail='Forbidden')
    if (  # If the user is invisible or far from the current user he cannot receive friend requests
        not user_model_to.visible
        or haversine_distance(user.latitude, user.longitude, user_model_to.latitude, user_model_to.longitude) > 500
    ):
        raise HTTPException(status_code=403, detail='Forbidden')
    if crud.get_request(db, user.id, user_to) or crud.get_request(db, user_to, user.id):
        raise HTTPException(status_code=400, detail='Friend request already sent')
    if crud.is_friend(db, user_to, user.id):
        raise HTTPException(status_code=409, detail='Already friends')
    crud.request_friend(db, user.id, user_to)
    return 'OK'


@router.delete(
    '/',
    status_code=200,
    responses={
        400: {'description': 'You cannot be friends with yourself'},
        403: {'description': 'Forbidden'},
        404: {'description': 'Friend request not found'},
    }
)
def delete_friend_request(user_to: int, user: models.User = Depends(get_user_dependency), db: Session = Depends(get_db)):
    if not crud.get_request(db, user.id, user_to):
        raise HTTPException(status_code=404, detail='Friend request not found')
    crud.delete_friend_request(db, user.id, user_to)
    return 'OK'
