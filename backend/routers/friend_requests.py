from fastapi import Request, APIRouter, Depends, HTTPException
from database import crud, schemas
from database.loader import get_db
from sqlalchemy.orm import Session


router = APIRouter(prefix='/requests')


@router.get('/', response_model=list[schemas.User], status_code=200)
def get_friend_requests(user_id: int, db: Session = Depends(get_db)):
    return crud.get_friend_requests(db, user_id)


@router.post(
    '/',
    status_code=201,
    responses={
        400: {'description': 'You cannot be friends with yourself'},
        403: {'description': 'Not implemented yet'},  # TODO
    }
)
def request_friend(user_from: int, user_to: int, db: Session = Depends(get_db)):
    if user_from == user_to:
        raise HTTPException(status_code=400, detail='You cannot be friends with yourself')
    crud.request_friend(db, user_from, user_to)
    return 'OK'


@router.delete('/', status_code=200)
def delete_friend_request(user_from: int, user_to: int, db: Session = Depends(get_db)):
    crud.delete_friend_request(db, user_from, user_to)
    return 'OK'
