from pydantic import BaseModel
from datetime import datetime


class UserCreate(BaseModel):
    nickname: str
    place: str
    latitude: float = None
    longitude: float = None


class UserUpdate(BaseModel):
    id: int
    nickname: str = None
    place: str = None
    latitude: float = None
    longitude: float = None
    visible: bool = None

    class Config:
        from_attributes = True


class User(BaseModel):
    id: int
    nickname: str
    place: str
    latitude: float
    longitude: float
    last_update: datetime

    class Config:
        from_attributes = True


class Profile(UserCreate):
    id: int
    last_update: datetime
    visible: bool
    friends: list[User]
    requests: list[User]
    near: list[User] | None
