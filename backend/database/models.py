from datetime import datetime
from sqlalchemy import ForeignKey, DateTime, Text, Boolean, func, BIGINT, VARCHAR, Double
from sqlalchemy.orm import relationship, DeclarativeBase, mapped_column, Mapped


class Base(DeclarativeBase):
    pass


class User(Base):
    __tablename__ = 'users'
    id: Mapped[int] = mapped_column(BIGINT, unique=True, primary_key=True)

    nickname: Mapped[str] = mapped_column(VARCHAR(25), nullable=True)
    place: Mapped[str] = mapped_column(VARCHAR(255), nullable=True)
    latitude: Mapped[float] = mapped_column(Double, nullable=True)
    longitude: Mapped[float] = mapped_column(Double, nullable=True)
    last_update: Mapped[datetime] = mapped_column(DateTime(timezone=True), default=func.now())
    visible: Mapped[bool] = mapped_column(Boolean, default=False)
    token: Mapped[str] = mapped_column(VARCHAR(64), nullable=True, unique=True)

    join_date: Mapped[datetime] = mapped_column(DateTime(timezone=True), default=func.now())

    def __repr__(self):
        return (f"User(id={self.id!r}, "
                f"nickname={self.nickname!r}, "
                f"join_date={self.join_date!r},"
                f"place={self.place!r},"
                f"coords=({self.longitude!r},{self.latitude!r}),"
                f"last_update={self.last_update!r})")


class Friendship(Base):
    __tablename__ = 'friendships'
    id: Mapped[int] = mapped_column(BIGINT, primary_key=True, autoincrement=True)
    user1_id: Mapped[int] = mapped_column(ForeignKey('users.id', ondelete='CASCADE'))
    user2_id: Mapped[int] = mapped_column(ForeignKey('users.id', ondelete='CASCADE'))
    date: Mapped[datetime] = mapped_column(DateTime(timezone=True), default=func.now())

    def __repr__(self):
        return (f"UserRelations(user1_id={self.user1_id!r}, "
                f"user2_id={self.user2_id!r})")


class FriendRequest(Base):
    __tablename__ = 'friend_requests'
    id: Mapped[int] = mapped_column(BIGINT, primary_key=True, autoincrement=True)
    user_from: Mapped[int] = mapped_column(ForeignKey('users.id', ondelete='CASCADE'))
    user_to: Mapped[int] = mapped_column(ForeignKey('users.id', ondelete='CASCADE'))
    date: Mapped[datetime] = mapped_column(DateTime(timezone=True), default=func.now())

    def __repr__(self):
        return (f"UserRelations(user_from={self.user_from!r}, "
                f"user_to={self.user_to!r})")
