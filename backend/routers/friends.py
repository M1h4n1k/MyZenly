from fastapi import Request, APIRouter, Depends, HTTPException
from database import crud, schemas
from database.loader import get_db
from sqlalchemy.orm import Session


router = APIRouter(prefix='/friends')


@router.get('/', response_model=list[schemas.Friend])
def get_friends(user_id: int, db: Session = Depends(get_db)):
    return crud.get_friends(db, user_id)


@router.post(
    '/',
    status_code=201,
    responses={
        400: {'description': 'You cannot be friends with yourself'},
        403: {'description': 'Not implemented yet'},  # TODO
        404: {'description': 'User not found'},  # TODO
    }
)
def add_friend(user1_id: int, user2_id: int, db: Session = Depends(get_db)):
    # TODO disallow adding friends without a friend request
    # so the flow is like: user1 requests user2 -> user2 accepts -> user1 and user2 are friends
    if user1_id == user2_id:
        raise HTTPException(status_code=400, detail='You cannot be friends with yourself')
    crud.add_friend(db, user1_id, user2_id)
    return 'OK'


@router.delete('/')
def delete_friend(user1_id: int, user2_id: int, db: Session = Depends(get_db)):
    crud.delete_friend(db, user1_id, user2_id)
    return 'OK'
