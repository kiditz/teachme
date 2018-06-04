import io
import os
import re

import requests
from flask import send_file
from flask_bcrypt import Bcrypt
from slerp import get_name_abbreviations, random_colors
from slerp.app import app
from slerp.exception import ValidationException
from slerp.logger import logging
from slerp.validator import Key, Number

from constant.api_constant import *
from constant.auth_constant import TEACHER_AUTHORITIES, STUDENT_AUTHORITIES
from entity.models import UserPrincipal, UserAuthority, Teacher, Student, Address

log = logging.getLogger(__name__)
bcrypt = Bcrypt(app=app)


class UserPrincipalService(object):
	def __init__(self):
		super(UserPrincipalService, self).__init__()
	
	@Key(['phone_number', 'username', 'fullname', 'gender', 'password', 'level_id', 'class_id'])
	def register_teacher(self, domain):
		self.validate_add_user(domain)
		user_dict = self.do_register(domain, authorities=TEACHER_AUTHORITIES)
		teacher = Teacher({'user_id': user_dict['id']})
		teacher.class_id = domain['class_id']
		teacher.level_id = domain['level_id']
		teacher.save()
		return {'payload': user_dict}
	
	@Key(['phone_number', 'username', 'fullname', 'gender', 'password'])
	def register_student(self, domain):
		self.validate_add_user(domain)
		user_dict = self.do_register(domain, authorities=STUDENT_AUTHORITIES)
		student = Student({'user_id': user_dict['id']})
		student.save()
		return {'payload': user_dict}
	
	@staticmethod
	def do_register(domain, authorities):
		domain['enabled'] = True
		domain['account_non_expired'] = True
		domain['account_non_locked'] = True
		domain['credentials_non_expired'] = True
		# Use $2a$ for java bcrypt specification
		domain['hash_password'] = bcrypt.generate_password_hash(domain['password'], 10).replace(b'$2b$', b'$2a$')
		log.info('password : %s', domain['hash_password'])
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
		user_principal = UserPrincipal.query.filter_by(username=domain['username']).first()
		if user_principal is None:
			raise ValidationException(USER_NOT_FOUND)
		return {'payload': user_principal.to_dict()}
	
	@Key(['username'])
	def get_user_image(self, domain):
		profile = UserPrincipal.query.filter_by(username=domain['username']).first()
		if profile is None:
			raise ValidationException(USER_NOT_FOUND)
		username = domain['username'] + '.png'
		base_dir = app.config['UPLOAD_FOLDER']
		path = os.path.join(base_dir, 'profile', username)
		log.info('Saved in >>> %s', path)
		# Checking directory must be exists if not just make dirs
		if not os.path.exists(os.path.dirname(path)):
			os.makedirs(os.path.dirname(path))
		# Checking if the image has been saved from place hold
		# if yes read from path if not call image from network then save it into profile folder
		if not os.path.exists(path):
			image = requests.get('https://place-hold.it/80x80/' + random_colors(
				use_hastag=False) + '/fff.png&text=' + get_name_abbreviations(
				name=username) + '&bold&fontsize=24', stream=True)
			if image.status_code == 200:
				with open(path, 'wb') as f:
					for chunk in image:
						f.write(chunk)
				with open(path, 'rb') as f:
					return send_file(io.BytesIO(f.read()), attachment_filename=username, mimetype='image/png')
			else:
				raise ValidationException(CONNECTION_ERROR)
		else:
			with open(path, 'rb') as f:
				return send_file(io.BytesIO(f.read()), attachment_filename=username, mimetype='image/png')
	
	@Key(['id', 'username', 'phone_number', 'fullname'])
	def edit_user_principal_by_username(self, domain):
		user_principal = UserPrincipal.query.get(domain['id'])
		
		if user_principal is None:
			raise ValidationException(USER_NOT_FOUND)
		
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
			raise ValidationException(INVALID_USERNAME)
		
		# Validate Phone Number exists
		val_phone_number = UserPrincipal.query.filter_by(phone_number=phone_number).first()
		if val_phone_number is not None:
			raise ValidationException(PHONE_NUMBER_HAS_BEEN_USED)
		
		# Validate Username exists
		val_username = UserPrincipal.query.filter_by(username=username).first()
		if val_username is not None:
			raise ValidationException(USERNAME_HAS_BEEN_USED)
		pass
	
	@staticmethod
	def validate_edit_user(domain, user):
		# Validation
		phone_number = domain['phone_number']
		username = domain['username']
		
		# Validate Username regex
		if not re.match(r'^[a-z0-9_-]{3,15}$', username):
			raise ValidationException(INVALID_USERNAME)
		
		# Validate Phone Number exists
		log.info('phone number %s : %s', user.phone_number, phone_number)
		log.info('phone number %s', user.phone_number != phone_number)
		if user.phone_number != phone_number:
			val_phone_number = UserPrincipal.query.filter_by(phone_number=phone_number).first()
			if val_phone_number is not None:
				raise ValidationException(PHONE_NUMBER_HAS_BEEN_USED)
		log.info('username %s', user.username != username)
		if user.username != username:
			val_username = UserPrincipal.query.filter_by(username=username).first()
			if val_username is not None:
				raise ValidationException(USERNAME_HAS_BEEN_USED)
		pass
	
	@Key(['username'])
	@Number(['page', 'size'])
	def get_user_principal_by_username(self, domain):
		page = int(domain['page'])
		size = int(domain['size'])
		user_principal_q = UserPrincipal.query.filter(
			UserPrincipal.username.ilike('%' + domain['username'] + '%')).order_by(
			UserPrincipal.username.asc()).paginate(page, size, error_out=False)
		user_principal_list = list(map(lambda x: x.to_dict(), user_principal_q.items))
		return {'payload': user_principal_list, 'total': user_principal_q.total, 'total_pages': user_principal_q.pages}
	
	@Key(['user_id'])
	def find_user_principal_by_id(self, domain):
		user_principal = UserPrincipal.query.filter_by(id=domain['user_id']).first()
		if user_principal is None:
			raise ValidationException(USER_NOT_FOUND)
		return {'payload': user_principal.to_dict()}
