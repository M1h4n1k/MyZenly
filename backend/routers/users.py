from fastapi import Request, APIRouter, Depends, HTTPException
from database import crud, schemas, models
from database.loader import get_db
from sqlalchemy.orm import Session


router = APIRouter(prefix='/users')


@router.post('/', response_model=schemas.User, status_code=201)
def create_user(user: schemas.UserCreate, db: Session = Depends(get_db)):
    return crud.create_user(db, models.User(**user.dict()))


@router.put(
    '/',
    status_code=200,
    responses={
        403: {'description': 'Not implemented yet'},  # TODO
        404: {'description': 'User not found'}
    }
)
def update_user(user: schemas.UserUpdate, db: Session = Depends(get_db)):
    if crud.get_user(db, user.id) is None:
        raise HTTPException(status_code=404, detail='User not found')
    crud.update_user(db, user)
    return 'OK'


@router.get(
    '/{user_id}',
    response_model=schemas.Profile,
    status_code=200,
    responses={
        403: {'description': 'Not implemented yet'},  # TODO
        404: {'description': 'User not found'}
    }
)
def get_user(user_id: int, db: Session = Depends(get_db)):
    user = crud.get_user(db, user_id)
    if not user:
        raise HTTPException(status_code=404, detail='User not found')
    friends = crud.get_friends(db, user_id)
    requests = crud.get_friend_requests(db, user_id)
    near = crud.get_near_users(user, db) if user.visible else None

    return schemas.Profile(**user.__dict__, friends=friends, requests=requests, near=near)
