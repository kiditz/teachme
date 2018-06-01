# coding: utf-8
from sqlalchemy import BigInteger, Boolean, Column, DateTime, ForeignKey, LargeBinary, String, Table, text
from sqlalchemy.orm import relationship
from sqlalchemy.ext.declarative import declarative_base


Base = declarative_base()
metadata = Base.metadata


class Client(Base):
    __tablename__ = 'client'

    id = Column(BigInteger, primary_key=True, server_default=text("nextval('client_seq'::regclass)"))
    client_id = Column(String(255), unique=True)
    client_secret = Column(String(255))


t_client_grant = Table(
    'client_grant', metadata,
    Column('client_id', String(255), nullable=False),
    Column('grant_name', String(255))
)


t_client_redirect = Table(
    'client_redirect', metadata,
    Column('client_id', String(255), nullable=False),
    Column('redirect_uri', String(255))
)


t_client_scope = Table(
    'client_scope', metadata,
    Column('client_id', String(255), nullable=False),
    Column('scope', String(255))
)


class UserAuthority(Base):
    __tablename__ = 'user_authority'

    authority_id = Column(BigInteger, primary_key=True, server_default=text("nextval('user_authority_seq'::regclass)"))
    authority = Column(String(255))
    user_id = Column(ForeignKey(u'user_principal.user_id'))

    user = relationship(u'UserPrincipal')


class UserPrincipal(Base):
    __tablename__ = 'user_principal'

    user_id = Column(BigInteger, primary_key=True)
    account_non_expired = Column(Boolean)
    account_non_locked = Column(Boolean)
    activation_code = Column(String(8), unique=True)
    created_at = Column(DateTime)
    credentials_non_expired = Column(Boolean)
    enabled = Column(Boolean)
    hashed_password = Column(LargeBinary, nullable=False)
    phone_number = Column(String(20), nullable=False)
    update_at = Column(DateTime)
