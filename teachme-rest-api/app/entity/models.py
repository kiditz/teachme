from datetime import datetime

from flask_migrate import Migrate, MigrateCommand
from flask_script import Manager
from slerp.app import app
from slerp.app import db
from slerp.entity import Entity

__author__ = "Rifky Aditya Bastara"


t_client_grant = db.Table(
	'tm_client_grant', db.metadata,
	db.Column('client_id', db.String(255), nullable=False),
	db.Column('grant_name', db.String(255), nullable=False)
)

t_client_redirect = db.Table(
	'tm_client_redirect', db.metadata,
	db.Column('client_id', db.String(255), nullable=False),
	db.Column('redirect_uri', db.String(255))
)

t_client_scope = db.Table(
	'tm_client_scope', db.metadata,
	db.Column('client_id', db.String(255), nullable=False),
	db.Column('scope', db.String(255))
)


class Client(db.Model, Entity):
	__tablename__ = 'tm_client'
	id = db.Column('client_oauth_id', db.BigInteger, db.Sequence('tm_client_client_oauth_id_seq'), primary_key=True)
	client_id = db.Column(db.String(255), unique=True)
	client_secret = db.Column(db.String(255))


class Address(db.Model, Entity):
	__tablename__ = 'tm_address'
	id = db.Column('address_id', db.BigInteger, db.Sequence('tm_user_user_id_seq'), primary_key=True)
	address = db.Column(db.Text)
	latitude = db.Column(db.BigInteger, default=-1)
	longitude = db.Column(db.BigInteger, default=-1)
	created_at = db.Column(db.DateTime(timezone=False), default=datetime.now)
	update_at = db.Column(db.DateTime(timezone=False), onupdate=datetime.now)

	def __init__(self, obj=None):
		Entity.__init__(self, obj)


class Document(db.Model, Entity):
	__tablename__ = 'tm_document'
	id = db.Column('document_id', db.BigInteger, db.Sequence('tm_document_document_id_seq'), primary_key=True)
	filename = db.Column(db.Text, nullable=False)
	original_filename = db.Column(db.Text, nullable=False, server_default='')
	thumbnails = db.Column(db.Text)
	mimetype = db.Column(db.String(60), nullable=False)
	folder = db.Column(db.Text, nullable=False)
	secure = db.Column(db.Boolean, nullable=False, server_default='t')
	created_at = db.Column(db.DateTime(timezone=False), default=datetime.now)
	update_at = db.Column(db.DateTime(timezone=False), onupdate=datetime.now)
	
	def __init__(self, obj=None):
		Entity.__init__(self, obj)
		
		
class UserPrincipal(db.Model, Entity):
	__tablename__ = 'tm_user'
	id = db.Column('user_id', db.BigInteger, db.Sequence('tm_user_user_id_seq'), primary_key=True)
	phone_number = db.Column(db.String(20), nullable=False, unique=True)
	username = db.Column(db.String(60), nullable=False, unique=True)
	fullname = db.Column(db.String(100), nullable=False)
	gender = db.Column(db.String(1), nullable=False)
	hash_password = db.Column(db.LargeBinary(60), nullable=False)
	address_id = db.Column(db.ForeignKey(u'tm_address.address_id'), nullable=True, index=True)
	enabled = db.Column(db.Boolean, nullable=False, default=False)
	account_non_expired = db.Column(db.Boolean, nullable=False, default=False)
	account_non_locked = db.Column(db.Boolean, nullable=False, default=False)
	credentials_non_expired = db.Column(db.Boolean, nullable=False, default=False)
	
	created_at = db.Column(db.DateTime(timezone=False), default=datetime.now)
	update_at = db.Column(db.DateTime(timezone=False), onupdate=datetime.now)
	__json_hidden__ = ['hash_password', 'enabled', 'account_non_expired', 'account_non_locked', 'credentials_non_expired']
	
	def __init__(self, obj=None):
		Entity.__init__(self, obj)
	
	def to_dict(self):
		return super().to_dict()


class UserAuthority(db.Model, Entity):
	__tablename__ = 'tm_user_authority'
	id = db.Column('user_authority_id', db.BigInteger, db.Sequence('tm_user_authority_user_authority_id_seq'), primary_key=True)
	authority = db.Column(db.String(255))
	user_id = db.Column(db.ForeignKey(u'tm_user.user_id'), nullable=False)


class School(db.Model, Entity):
	__tablename__ = 'tm_school'
	
	def __init__(self, obj=None):
		Entity.__init__(self, obj)
		
	id = db.Column('school_id', db.BigInteger, db.Sequence('tm_school_school_id_seq'), primary_key=True)
	name = db.Column(db.String(100), nullable=False, unique=True)
	description = db.Column(db.Text, nullable=False)
	url = db.Column(db.Text)
	address_id = db.Column(db.ForeignKey(u'tm_address.address_id'), nullable=True, index=True)
	user_id = db.Column(db.ForeignKey(u'tm_user.user_id'), nullable=False, index=True)
	active = db.Column(db.Boolean, default=True)
	non_active_at = db.Column(db.DateTime(timezone=False))
	active_at = db.Column(db.DateTime(timezone=False), default=datetime.now)
	created_at = db.Column(db.DateTime(timezone=False), default=datetime.now)
	update_at = db.Column(db.DateTime(timezone=False), onupdate=datetime.now)
	document_id = db.Column(db.ForeignKey(u'tm_document.document_id'), nullable=False, index=True)


class Student(db.Model, Entity):
	__tablename__ = 'tm_student'
	id = db.Column('student_id', db.BigInteger, db.Sequence('tm_student_student_id_seq'), primary_key=True)
	user_id = db.Column(db.ForeignKey(u'tm_user.user_id'), nullable=False, index=True)
	school_id = db.Column(db.ForeignKey(u'tm_school.school_id'), index=True)
	accept_by = db.Column(db.BigInteger, nullable=False, default=-99)
	created_at = db.Column(db.DateTime(timezone=False), default=datetime.now)
	update_at = db.Column(db.DateTime(timezone=False), onupdate=datetime.now)
	db.UniqueConstraint('school_id', 'user_id', name='uix_student')
	class_id = db.Column(db.ForeignKey(u'tm_school_class.school_class_id'), index=True)
	level_id = db.Column(db.ForeignKey(u'tm_school_level.school_level_id'), index=True)
	
	def __init__(self, obj=None):
		Entity.__init__(self, obj)
		

class Teacher(db.Model, Entity):
	__tablename__ = 'tm_teacher'
	id = db.Column('teacher_id', db.BigInteger, db.Sequence('tm_teacher_teacher_id_seq'), primary_key=True)
	user_id = db.Column(db.ForeignKey(u'tm_user.user_id'), nullable=False, index=True)
	school_id = db.Column(db.ForeignKey(u'tm_school.school_id'), index=True)
	class_id = db.Column(db.ForeignKey(u'tm_school_class.school_class_id'), index=True)
	level_id = db.Column(db.ForeignKey(u'tm_school_level.school_level_id'), index=True)
	accept_by = db.Column(db.BigInteger, nullable=False, default=-99)
	created_at = db.Column(db.DateTime(timezone=False), default=datetime.now)
	update_at = db.Column(db.DateTime(timezone=False), onupdate=datetime.now)
	user = db.relationship(u'UserPrincipal')
	__json_hidden__ = ['user.hash_password', 'user.account_non_expired', 'user.credentials_non_expired',
	                   'user.account_non_locked']
	db.UniqueConstraint('school_id', 'user_id', name='uix_teacher')
	
	def __init__(self, obj=None):
		Entity.__init__(self, obj)


class SchoolLevel(db.Model, Entity):
	__tablename__ = 'tm_school_level'
	id = db.Column('school_level_id', db.BigInteger, db.Sequence('tm_school_level_school_level_id'), primary_key=True)
	name = db.Column(db.String(30), nullable=False, index=True)
	sort_number = db.Column(db.BigInteger, nullable=False)
	user_id = db.Column(db.ForeignKey(u'tm_user.user_id'), nullable=False, index=True)
	created_at = db.Column(db.DateTime(timezone=False), default=datetime.now)
	update_at = db.Column(db.DateTime(timezone=False), onupdate=datetime.now)
	
	
class SchoolClass(db.Model, Entity):
	__tablename__ = 'tm_school_class'
	id = db.Column('school_class_id', db.BigInteger, db.Sequence('tm_school_class_school_class_id'), primary_key=True)
	name = db.Column(db.String(30), nullable=False, index=True)
	level_id = db.Column(db.ForeignKey(u'tm_school_level.school_level_id'), nullable=False, index=True)
	
	user_id = db.Column(db.ForeignKey(u'tm_user.user_id'), nullable=False, index=True)
	created_at = db.Column(db.DateTime(timezone=False), default=datetime.now)
	update_at = db.Column(db.DateTime(timezone=False), onupdate=datetime.now)


class MaterialTopic(db.Model, Entity):
	__tablename__ = 'tm_material_topic'
	id = db.Column('topic_id', db.BigInteger, db.Sequence('tm_material_topic_topic_id_seq'), primary_key=True)
	name = db.Column(db.Text, nullable=False)
	user_id = db.Column(db.ForeignKey(u'tm_user.user_id'), nullable=False, index=True)
	level_id = db.Column(db.ForeignKey(u'tm_school_level.school_level_id'), index=True)
	class_id = db.Column(db.ForeignKey(u'tm_school_class.school_class_id'), index=True)
	created_at = db.Column(db.DateTime(timezone=False), default=datetime.now)
	update_at = db.Column(db.DateTime(timezone=False), onupdate=datetime.now)
	
	def __init__(self, obj=None):
		Entity.__init__(self, obj)

	
class Material(db.Model, Entity):
	__tablename__ = 'tm_material'
	id = db.Column('material_id', db.BigInteger, db.Sequence('tm_material_material_id_seq'), primary_key=True)	
	title = db.Column(db.String(140), nullable=False)	
	type = db.Column(db.String(20), nullable=False, default='video')
	document_id = db.Column(db.ForeignKey(u'tm_document.document_id'), nullable=False, index=True)
	price = db.Column(db.Numeric, nullable=False)
	user_id = db.Column(db.ForeignKey(u'tm_user.user_id'), nullable=False, index=True)
	topic_id = db.Column(db.ForeignKey(u'tm_material_topic.topic_id'), index=True)
	user = db.relationship(u'UserPrincipal')
	active = db.Column(db.Boolean, nullable=False, server_default='f')
	created_at = db.Column(db.DateTime(timezone=False), default=datetime.now)
	update_at = db.Column(db.DateTime(timezone=False), onupdate=datetime.now)
	# Hiding the json key in http get
	__json_hidden__ = ['user.hash_password', 'user.account_non_expired', 'user.credentials_non_expired', 'user.account_non_locked']
	
	def __init__(self, obj=None):
		Entity.__init__(self, obj)
	

class Activity(db.Model, Entity):
	__tablename__ = 'tm_activity'
	id = db.Column('activity_id', db.BigInteger, db.Sequence('tm_activity_activity_id_seq'), primary_key=True)
	message = db.Column(db.Text, nullable=False)
	user_id = db.Column(db.ForeignKey(u'tm_user.user_id'), nullable=False, index=True)
	raw = db.Column(db.Text, nullable=False, server_default='-', default='')
	created_at = db.Column(db.DateTime(timezone=False), default=datetime.now)
	update_at = db.Column(db.DateTime(timezone=False), onupdate=datetime.now)
	user = db.relationship(u'UserPrincipal')
	doc_type = db.Column(db.String(20))
	__json_hidden__ = ['user.hash_password', 'user.account_non_expired', 'user.credentials_non_expired',
	                   'user.account_non_locked']
	
	def __init__(self, obj=None):
		Entity.__init__(self, obj)
	

class Friend(db.Model, Entity):
	__tablename__ = 'tm_friend'
	user_id = db.Column(db.ForeignKey(u'tm_user.user_id'), nullable=False, primary_key=True)
	friend_id = db.Column(db.ForeignKey(u'tm_user.user_id'), nullable=False, primary_key=True)
	status = db.Column(db.String(10), nullable=False, server_default='')
	created_at = db.Column(db.DateTime(timezone=False), default=datetime.now)
	update_at = db.Column(db.DateTime(timezone=False), onupdate=datetime.now)
	
	def __init__(self, obj=None):
		Entity.__init__(self, obj)
	

if __name__ == '__main__':
	app.config['SQLALCHEMY_DATABASE_URI'] = 'postgresql://kiditz:rioters7@172.17.0.1:2070/teachme'
	migrate = Migrate(app, db)
	manager = Manager(app)
	manager.add_command('db', MigrateCommand)
	manager.run()
