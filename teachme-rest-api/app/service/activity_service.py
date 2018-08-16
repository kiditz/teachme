from slerp.logger import logging
from slerp.validator import Key, Number
from sqlalchemy import func, case

from flask import json
from constant.api_constant import ActivityType
from entity.models import Activity, Friend, UserPrincipal, SchoolClass, SchoolLevel, Lesson, LessonViewer

log = logging.getLogger(__name__)


class ActivityService(object):
	def __init__(self):
		super(ActivityService, self).__init__()
	
	@Key(['message', 'user_id'])
	def add_activity(self, domain):
		activity = Activity(domain)
		activity.save()
		return {'payload': activity.to_dict()}
	
	@Number(['page', 'size', 'user_id'])
	def get_activity_by_user_id(self, domain):
		page = int(domain['page'])
		size = int(domain['size'])
		title = case([(UserPrincipal.id == domain['user_id'], 'Anda')], else_=UserPrincipal.fullname).label('title')
		entities = (
			Activity.id,
			Activity.doc_type,
			Activity.message,
			Activity.created_at,
			Activity.user_id,
			Activity.raw,
			UserPrincipal.username,
			UserPrincipal.fullname,
			title
		)

		activity_q = Activity.query.with_entities(*entities) \
				.outerjoin(Friend, Friend.friend_id == Activity.user_id) \
				.join(UserPrincipal, UserPrincipal.id == Activity.user_id) \
				.filter(Activity.user_id == domain['user_id']) \
				.order_by(Activity.id.desc()).group_by(*entities).paginate(page, size, error_out=False)
		activity_list = list(map(lambda x: self.handle_detail(x._asdict()), activity_q.items))
		return {'payload': activity_list, 'total': activity_q.total, 'total_pages': activity_q.pages}
	
	@Number(['page', 'size', 'user_id'])
	def get_all_activity(self, domain):
		page = int(domain['page'])
		size = int(domain['size'])
		title = case([(UserPrincipal.id == domain['user_id'], 'Anda')], else_=UserPrincipal.fullname).label('title')
		# Get activity from friend
		entities = (
			Activity.id,
			Activity.doc_type,
			Activity.message,
			Activity.created_at,
			Activity.user_id,
			Activity.raw,
			UserPrincipal.username,
			UserPrincipal.fullname,
			title
		)
		
		activity_q1 = Activity.query.with_entities(*entities) \
			.join(Friend, Friend.friend_id == Activity.user_id) \
			.join(UserPrincipal, UserPrincipal.id == Activity.user_id) \
			.filter(Friend.user_id == domain['user_id']) \
			.filter(Activity.created_at >= Friend.created_at) \
			.filter(Activity.doc_type != ActivityType.UNFOLLOW) \
		
		# Get activity from user
		activity_q2 = Activity.query.with_entities(*entities) \
			.join(UserPrincipal, UserPrincipal.id == Activity.user_id) \
			.filter(Activity.user_id == domain['user_id'])

		# Merging activity by using union
		query = activity_q1.union(activity_q2).order_by(Activity.created_at.desc()).paginate(page, size, error_out=False)
		activity_list = list(map(lambda x: self.handle_detail(x._asdict()), query.items))
		return {'payload': activity_list, 'total': query.total, 'total_pages': query.pages}

	@staticmethod
	def handle_detail(activity):
		raw = json.loads(str(activity['raw']).encode('utf-8'))
		activity['raw'] = raw
		# Detail is required key document_id, title and detail, icon_name
		if activity['doc_type'] == ActivityType.FOLLOW or activity['doc_type'] == ActivityType.UNFOLLOW:
			# Translate register_type to indonesian language
			register_type = case([
				(UserPrincipal.register_type == 'STUDENT', 'Siswa')
			], else_='Guru').label('type')
			
			
			# Process query using upper concat and case expr
			user_dict = UserPrincipal.query.with_entities(
				UserPrincipal.fullname.label('title'),
				func.coalesce(UserPrincipal.document_id, -1).label('document_id'),
				func.upper(
					func.concat(
						register_type, ' ',
						func.coalesce(SchoolClass.name, ''), ' ',
						SchoolLevel.name
					)
				).label('detail')
			).outerjoin(SchoolClass, SchoolClass.id == UserPrincipal.class_id) \
				.join(SchoolLevel, SchoolLevel.id == UserPrincipal.level_id) \
				.filter(UserPrincipal.id == raw['friend_id']).first()._asdict()
			activity['detail'] = user_dict
		elif activity['doc_type'] == ActivityType.LESSON or activity['doc_type'] == ActivityType.LESSON_VIEWER:
			detail = LessonViewer.query.filter_by(lesson_id=raw['id']).count()
			entities = (Lesson.document_id, Lesson.title)
			lesson_dict = Lesson.query.with_entities(*entities).filter(Lesson.id == raw['id']).first()._asdict()
			lesson_dict['detail'] = ' ' + str(detail)
			activity['detail'] = lesson_dict
			pass
		else:
			activity['detail'] = None
		return activity
