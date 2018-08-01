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
from api.user_principal_api import user_principal_service
from constant.api_constant import ActivityType, ActivityMessage, ErrorCode
from entity.models import Lesson, Topic

log = logging.getLogger(__name__)


class LessonService(object):
	def __init__(self):
		super(LessonService, self).__init__()
	
	@Blank(['title', 'type'])
	@Number(['document_id', 'user_id', 'price'])
	def add_lesson(self, domain):
		user = user_principal_service.find_user_principal_by_id(domain)['payload']
		if 'topic_id' not in domain:
			topic = Topic({'user_id': user['id']})
			if is_blank(domain['name']):
				raise ValidationException('required.value.name')
			topic.name = domain['name']
			topic.class_id = user['class_id'] if 'class_id' in user else None
			topic.level_id = user['level_id']
			topic.save()
		else:
			topic = Topic.query.get(domain['topic_id'])
			domain.pop('topic_id')
		pass
		lesson = Lesson(domain)
		lesson.topic_id = topic.id
		lesson.save()
		lesson_dict = lesson.to_dict()
		return {'payload': lesson_dict}
	
	@Key(['title'])
	@Number(['page', 'size'])
	def get_lesson_by_title(self, domain):
		title = domain['title']
		page = int(domain['page'])
		size = int(domain['size'])
		lesson_q = Lesson.query.filter(Lesson.title.ilike('%' + title + '%')).order_by(
			Lesson.id.desc()).paginate(page, size, error_out=False)
		
		lesson_list = list(map(lambda x: x.to_dict(), lesson_q.items))
		return {'payload': lesson_list, 'total': lesson_q.total, 'total_pages': lesson_q.pages}
	
	@Number(['page', 'size', 'topic_id'])
	def get_lesson_by_topic_id(self, domain):
		page = int(domain['page'])
		size = int(domain['size'])
		lesson_q = Lesson.query.filter_by(topic_id=domain['topic_id']).order_by(Lesson.title.asc()) \
			.filter_by(active='A').paginate(page, size, error_out=False)
		lesson_list = list(map(lambda x: x.to_dict(), lesson_q.items))
		return {'payload': lesson_list, 'total': lesson_q.total, 'total_pages': lesson_q.pages}
	
	@Key(['id'])
	def get_lesson_image(self, domain):
		lesson = Lesson.query.get(domain['id'])
		if lesson is None:
			raise ValidationException(ErrorCode.LESSON_NOT_FOUND)
		title = secure_filename(lesson.title + '.png')
		base_dir = app.config['UPLOAD_FOLDER']
		path = os.path.join(base_dir, 'lesson_image', title)
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
				raise ValidationException(ErrorCode.CONNECTION_ERROR)
		else:
			with open(path, 'rb') as f:
				return send_file(io.BytesIO(f.read()), attachment_filename=title, mimetype='image/png')
	
	@Key(['title'])
	@Number(['page', 'size', 'user_id'])
	def get_lesson_by_user_id(self, domain):
		title = domain['title']
		page = int(domain['page'])
		size = int(domain['size'])
		lesson_q = Lesson.query.filter_by(user_id=domain['user_id']).filter(Lesson.title.ilike('%' + title + '%')) \
			.order_by(Lesson.id.desc()).paginate(page, size, error_out=False)
		lesson_list = list(map(lambda x: x.to_dict(), lesson_q.items))
		return {'payload': lesson_list, 'total': lesson_q.total, 'total_pages': lesson_q.pages}
	
	@Key(['id'])
	def find_lesson_by_id(self, domain):
		lesson = Lesson.query.filter_by(id=domain['id']).first()
		return {'payload': lesson.to_dict()}
	
	@Number(['id'])
	def activate_lesson(self, domain):
		lesson = Lesson.query.filter_by(id=domain['id']).first()
		if lesson is None:
			raise ValidationException(ErrorCode.LESSON_NOT_FOUND)
		if lesson.active != 'I':
			raise ValidationException(ErrorCode.ONLY_IN_PROGRESS_CAN_BE_ACTIVATED)
		lesson.active = domain['active']
		lesson.save()
		lesson_dict = lesson.to_dict()
		lesson_dict.pop("user")
		if lesson.active == 'A':
			activity_domain = {'user_id': lesson.user_id,
			                   'message': ActivityMessage.NEW_LESSON,
			                   'raw': json.dumps(lesson_dict, indent=4, sort_keys=True),
			                   'doc_type': ActivityType.LESSON}
			activity_service.add_activity(activity_domain)
		return {'payload': lesson_dict}
