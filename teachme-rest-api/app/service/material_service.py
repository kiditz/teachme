import io
import os

import requests
from flask import send_file, json
from slerp.app import app
from slerp.logger import logging
from slerp.string_utils import is_blank, random_colors, get_name_abbreviations
from slerp.validator import Number, Blank, Key, ValidationException
from werkzeug.utils import secure_filename

from api.activity_api import activity_service
from api.teacher_api import teacher_service
from constant.api_constant import MATERIAL_NOT_FOUND, CONNECTION_ERROR, ACTIVITY_TYPE_MATERIAL, ONLY_IN_PROGRESS_CAN_BE_ACTIVATED
from entity.models import Material, MaterialTopic

log = logging.getLogger(__name__)


class MaterialService(object):
	def __init__(self):
		super(MaterialService, self).__init__()
	
	@Blank(['title', 'description', 'type'])
	@Number(['document_id', 'user_id', 'price'])
	def add_material(self, domain):
		# Teacher variable is for validation and used to fill class id and level id
		teacher = teacher_service.find_teacher_by_user_id(domain)['payload']
		if 'topic_id' not in domain:
			topic = MaterialTopic({'user_id': teacher['user_id']})
			if is_blank(domain['name']):
				raise ValidationException('required.value.name')
			topic.name = domain['name']
			topic.class_id = teacher['class_id'] if 'class_id' in teacher else None
			topic.level_id = teacher['level_id']
			topic.save()
		else:
			topic = MaterialTopic.query.get(domain['topic_id'])
			domain.pop('topic_id')
		pass
		material = Material(domain)
		material.topic_id = topic.id
		material.save()
		return {'payload': material.to_dict()}
	
	@Key(['title'])
	@Number(['page', 'size'])
	def get_material_by_title(self, domain):
		title = domain['title']
		page = int(domain['page'])
		size = int(domain['size'])
		material_q = Material.query.filter(Material.title.ilike('%' + title + '%')).order_by(
			Material.id.desc()).paginate(page, size, error_out=False)
		
		material_list = list(map(lambda x: x.to_dict(), material_q.items))
		return {'payload': material_list, 'total': material_q.total, 'total_pages': material_q.pages}
	
	@Number(['page', 'size', 'topic_id'])
	def get_material_by_topic_id(self, domain):
		page = int(domain['page'])
		size = int(domain['size'])
		material_q = Material.query.filter_by(topic_id=domain['topic_id']).order_by(Material.title.asc()) \
			.filter_by(active='A').paginate(page, size, error_out=False)
		material_list = list(map(lambda x: x.to_dict(), material_q.items))
		return {'payload': material_list, 'total': material_q.total, 'total_pages': material_q.pages}
	
	@Key(['id'])
	def get_material_image(self, domain):
		material = Material.query.get(domain['id'])
		if material is None:
			raise ValidationException(MATERIAL_NOT_FOUND)
		title = secure_filename(material.title + '.png')
		base_dir = app.config['UPLOAD_FOLDER']
		path = os.path.join(base_dir, 'material_image', title)
		log.info('Saved in >>> %s', path)
		# Checking directory must be exists if not just make dirs
		if not os.path.exists(os.path.dirname(path)):
			os.makedirs(os.path.dirname(path))
		# Checking if the image has been saved from place hold
		# if yes read from path if not call image from network then save it into profile folder
		if not os.path.exists(path):
			image = requests.get('https://place-hold.it/256x256/' + random_colors(
				use_hastag=False) + '/fff.png&text=' + get_name_abbreviations(
				name=title) + '&bold&fontsize=32', stream=True)
			if image.status_code == 200:
				with open(path, 'wb') as f:
					for chunk in image:
						f.write(chunk)
				with open(path, 'rb') as f:
					return send_file(io.BytesIO(f.read()), attachment_filename=title, mimetype='image/png')
			else:
				raise ValidationException(CONNECTION_ERROR)
		else:
			with open(path, 'rb') as f:
				return send_file(io.BytesIO(f.read()), attachment_filename=title, mimetype='image/png')
	
	@Key(['title'])
	@Number(['page', 'size', 'user_id'])
	def get_material_by_user_id(self, domain):
		title = domain['title']
		page = int(domain['page'])
		size = int(domain['size'])
		material_q = Material.query.filter_by(user_id=domain['user_id']).filter(Material.title.ilike('%' + title + '%'))\
			.order_by(Material.id.desc()).paginate(page, size, error_out=False)
		material_list = list(map(lambda x: x.to_dict(), material_q.items))
		return {'payload': material_list, 'total': material_q.total, 'total_pages': material_q.pages}
	
	@Key(['id'])
	def find_material_by_id(self, domain):
		material = Material.query.filter_by(id=domain['id']).first()
		return {'payload': material.to_dict()}
	
	@Number(['id'])
	def activate_material(self, domain):
		material = Material.query.filter_by(id=domain['id']).first()
		if material is None:
			raise ValidationException(MATERIAL_NOT_FOUND)
		if material.active != 'I':
			raise ValidationException(ONLY_IN_PROGRESS_CAN_BE_ACTIVATED)
		material.update(domain)
		if material.active == 'A':
			activity_domain = {'user_id': material.user_id,
			                   'message': 'membuat materi',
			                   'raw': json.dumps({'id': material.id}),
			                   'doc_type': ACTIVITY_TYPE_MATERIAL}
			activity_service.add_activity(activity_domain)
		return {'payload': material.to_dict()}