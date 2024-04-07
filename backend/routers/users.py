from fastapi import Request, APIRouter, Depends, HTTPException
from database import crud, schemas, models
from database.loader import get_db
from sqlalchemy.orm import Session
import os
from .dependencies import get_user as get_user_dependency

router = APIRouter(prefix='/users')


@router.post('/', response_model=str, status_code=201)
def create_user(user: schemas.UserCreate, db: Session = Depends(get_db)):
    """
        I feel like it's not a really cool way, but I don't want to overcomplicate it and use some complex
        authorization. There is might be a security issue with catching the token by a 3rd party and using it later
        and a problem of logging in to the account after changing the device. But since it's not a real world project,
        I will leave it as it is for the sake of simplicity
    """
    token = os.urandom(32).hex()
    crud.create_user(db, models.User(**user.dict(), token=token))
    return token


@router.put(
    '/',
    status_code=200,
    responses={
        403: {'description': 'Forbidden'},
        404: {'description': 'User not found'}
    }
)
def update_user(user_upd: schemas.UserUpdate, user: models.User = Depends(get_user_dependency), db: Session = Depends(get_db)):
    if user_upd.id != user.id:
        raise HTTPException(status_code=403, detail='Forbidden')
    crud.update_user(db, user_upd)
    return 'OK'


@router.get(
    '/',
    response_model=schemas.Profile,
    status_code=200,
    responses={
        403: {'description': 'Forbidden'},
        404: {'description': 'User not found'}
    }
)
def get_user(user: models.User = Depends(get_user_dependency), db: Session = Depends(get_db)):
    friends = crud.get_friends(db, user.id)
    requests = crud.get_friend_requests(db, user.id)
    near = crud.get_near_users(user, db) if user.visible else None

    return schemas.Profile(**user.__dict__, friends=friends, requests=requests, near=near)
