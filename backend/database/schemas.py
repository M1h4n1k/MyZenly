from pydantic import BaseModel
from datetime import datetime


class UserCreate(BaseModel):
    nickname: str
    place: str
    coords: str


class UserUpdate(BaseModel):
    nickname: str
    place: str
    coords: str
    visible: bool


class User(UserCreate):
    id: int
    last_update: datetime
    visible: bool


class Friend(BaseModel):
    id: int
    nickname: str
    place: str
    coords: str
    last_update: datetime
