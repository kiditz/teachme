import os
import re

from entity.models import UserPrincipal, UserAuthority, Address, SchoolClass, SchoolLevel, Friend, Document
from flask import send_file
from flask_bcrypt import Bcrypt
from slerp.app import app
from slerp.exception import ValidationException
from slerp.logger import logging
from slerp.validator import Key

from constant.api_constant import ErrorCode, UserType
from constant.auth_constant import TEACHER_AUTHORITIES, STUDENT_AUTHORITIES

log = logging.getLogger(__name__)
bcrypt = Bcrypt(app=app)


class UserPrincipalService(object):
	def __init__(self):
		super(UserPrincipalService, self).__init__()
	
	@Key(['phone_number', 'username', 'fullname', 'gender', 'password', 'level_id', 'class_id', 'is_teacher'])
	def register(self, domain):
		self.validate_add_user(domain)
		if domain['is_teacher']:
			domain['register_type'] = UserType.TEACHER
			user_dict = self.do_register(domain, TEACHER_AUTHORITIES)
		else:
			domain['register_type'] = UserType.STUDENT
			user_dict = self.do_register(domain, STUDENT_AUTHORITIES)
		return {'payload': user_dict}
	
	@staticmethod
	def do_register(domain, authorities):
		domain['enabled'] = True
		domain['account_non_expired'] = True
		domain['account_non_locked'] = True
		domain['credentials_non_expired'] = True
		# Use $2a$ for java bcrypt specification
		
		domain['hash_password'] = bcrypt.generate_password_hash(domain['password'], 10).replace(b'$2b$', b'$2a$')
		user_principal = UserPrincipal(domain)
		if 'address' in domain:
			user_principal.address = Address({'address': domain['address']})
		user_principal.save()
		for role in authorities:
			user_authority = UserAuthority()
			user_authority.authority = role
			user_authority.user_id = user_principal.id
			user_authority.save()
		user_dict = user_principal.to_dict()
		return user_dict
	
	@Key(['username'])
	def find_user_principal_by_username(self, domain):
		user_principal = UserPrincipal.query.with_entities(
			UserPrincipal.id,
			UserPrincipal.phone_number,
			UserPrincipal.username,
			UserPrincipal.fullname,
			UserPrincipal.gender,
			SchoolLevel.name.label('level'),
			SchoolClass.name.label('class_name'),
			UserPrincipal.level_id,
			UserPrincipal.class_id
		).join(SchoolLevel, UserPrincipal.level_id == SchoolLevel.id) \
			.outerjoin(SchoolClass, UserPrincipal.class_id == SchoolClass.id) \
			.filter(UserPrincipal.username == domain['username']).first()
		if user_principal is None:
			raise ValidationException(ErrorCode.USER_NOT_FOUND)
		user_dict = user_principal._asdict()
	
		return {'payload': user_dict}
	
	@Key(['username'])
	def get_user_image(self, domain):
		document = Document.query .join(UserPrincipal, UserPrincipal.document_id == Document.id) \
			.filter(UserPrincipal.username == domain['username']).first()
		# User not found cause is load by username
		if document is None:
			raise ValidationException(ErrorCode.USER_NOT_FOUND)		
		else:
			filename = os.path.join(document.folder,
		                        document.filename if 'thumbnails' not in domain else document.thumbnails)
			return send_file(filename, mimetype=document.mimetype if 'thumbnails' not in domain else 'image/png')
				
	
	@Key(['id'])
	def edit_user_principal_by_username(self, domain):
		log.info("Input %s", domain)
		user_principal = UserPrincipal.query.get(domain['id'])
		
		if user_principal is None:
			raise ValidationException(ErrorCode.USER_NOT_FOUND)
		
		if 'username' in domain and 'phone_number' in domain:
			self.validate_edit_user(domain, user_principal)
			if 'password' in domain:			
				domain['hash_password'] = bcrypt.generate_password_hash(domain['password'], 10).replace(b'$2b$', b'$2a$')
		
			if domain['phone_number'].startswith('0'):
				domain['phone_number'] = domain['phone_number'].replace('0', '+62', 1)				
		
		user_principal.update(domain)
		user_dict = user_principal.to_dict()
		return {'payload': user_dict}
	
	@staticmethod
	def validate_add_user(domain):
		# Validation
		if domain['phone_number'].startswith('0'):
			domain['phone_number'] = domain['phone_number'].replace('0', '+62', 1)
		
		phone_number = domain['phone_number']
		username = domain['username']
		
		# Validate Username regex
		if not re.match(r'^[a-z0-9_-]{3,15}$', username):
			raise ValidationException(ErrorCode.INVALID_USERNAME)
		
		# Validate Phone Number exists
		val_phone_number = UserPrincipal.query.filter_by(phone_number=phone_number).first()
		if val_phone_number is not None:
			raise ValidationException(ErrorCode.PHONE_NUMBER_HAS_BEEN_USED)
		
		# Validate Username exists
		val_username = UserPrincipal.query.filter_by(username=username).first()
		if val_username is not None:
			raise ValidationException(ErrorCode.USERNAME_HAS_BEEN_USED)
		pass
	
	@staticmethod
	def validate_edit_user(domain, user):
		# Validation
		phone_number = domain['phone_number']
		username = domain['username']
		
		# Validate Username regex
		if not re.match(r'^[a-z0-9_-]{3,15}$', username):
			raise ValidationException(ErrorCode.INVALID_USERNAME)
		
		# Validate Phone Number exists
		log.info('phone number %s : %s', user.phone_number, phone_number)
		log.info('phone number %s', user.phone_number != phone_number)
		if user.phone_number != phone_number:
			val_phone_number = UserPrincipal.query.filter_by(phone_number=phone_number).first()
			if val_phone_number is not None:
				raise ValidationException(ErrorCode.PHONE_NUMBER_HAS_BEEN_USED)
		log.info('username %s', user.username != username)
		if user.username != username:
			val_username = UserPrincipal.query.filter_by(username=username).first()
			if val_username is not None:
				raise ValidationException(ErrorCode.USERNAME_HAS_BEEN_USED)
		pass
		
	@Key(['user_id', 'friend_id'])
	def find_user_principal_by_id(self, domain):
		user_principal = UserPrincipal.query.with_entities(
			UserPrincipal.id,
			UserPrincipal.phone_number,
			UserPrincipal.username,
			UserPrincipal.fullname,
			UserPrincipal.gender,
			SchoolLevel.name.label('level'),
			SchoolClass.name.label('class_name'),
			UserPrincipal.level_id,
			UserPrincipal.class_id
		).join(SchoolLevel, UserPrincipal.level_id == SchoolLevel.id) \
			.outerjoin(SchoolClass, UserPrincipal.class_id == SchoolClass.id) \
			.filter(UserPrincipal.id == domain['user_id']).first()
		if user_principal is None:
			raise ValidationException(ErrorCode.USER_NOT_FOUND)
		user_dict = user_principal._asdict()
		
		if int(domain['friend_id']) > 0:
			count_follower = Friend.query.filter_by(user_id=user_dict['id'], friend_id=domain['friend_id']).count()
			count_following = Friend.query.filter_by(user_id=domain['friend_id'], friend_id=user_dict['id']).count()
			user_dict['is_following'] = (count_following > 0)
			user_dict['is_follower'] = (count_follower > 0)
			
		return {'payload': user_dict}
