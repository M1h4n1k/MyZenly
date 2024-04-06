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
        409: {'description': 'Friend request already sent or already friends'},
    }
)
def request_friend(user_from: int, user_to: int, db: Session = Depends(get_db)):
    if user_from == user_to:
        raise HTTPException(status_code=400, detail='You cannot be friends with yourself')
    if crud.get_request(db, user_from, user_to) or crud.get_request(db, user_to, user_from):
        raise HTTPException(status_code=403, detail='Friend request already sent')
    if crud.is_friend(db, user_to, user_from):
        raise HTTPException(status_code=409, detail='Already friends')
    crud.request_friend(db, user_from, user_to)
    return 'OK'


@router.delete(
    '/',
    status_code=200,
    responses={
        400: {'description': 'You cannot be friends with yourself'},
        403: {'description': 'Not implemented yet'},  # TODO
        404: {'description': 'Friend request not found'},
    }
)
def delete_friend_request(user_from: int, user_to: int, db: Session = Depends(get_db)):
    if not crud.get_request(db, user_from, user_to):
        raise HTTPException(status_code=404, detail='Friend request not found')
    crud.delete_friend_request(db, user_from, user_to)
    return 'OK'
