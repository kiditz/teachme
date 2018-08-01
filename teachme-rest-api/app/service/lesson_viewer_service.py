import json

from slerp.exception import ValidationException
from slerp.logger import logging
from slerp.validator import Number
from utils.encoder import TeachmeJsonEncoder
from api.activity_api import activity_service
from constant.api_constant import ActivityType, ActivityMessage, ErrorCode
from entity.models import LessonViewer, Lesson

log = logging.getLogger(__name__)


class LessonViewerService(object):
	def __init__(self):
		super(LessonViewerService, self).__init__()

	@Number(['user_id', 'lesson_id'])
	def add_lesson_viewer(self, domain):
		lesson = Lesson.query.get(domain['lesson_id'])
		if lesson is None:
			raise ValidationException(ErrorCode.LESSON_NOT_FOUND)
		if lesson.active != 'A':
			raise ValidationException(ErrorCode.LESSON_IS_NOT_ACTIVE)
		viewer = LessonViewer.query.filter_by(user_id=domain['user_id']).filter_by(lesson_id=domain['lesson_id']).first()
		if viewer is not None:
			raise ValidationException(ErrorCode.LESSON_VIEWER_EXIST)
		if lesson.user_id == domain['user_id']:
			raise ValidationException(ErrorCode.LESSON_VIEWER_MUST_NOT_CREATOR)
		lesson_viewer = LessonViewer(domain)
		lesson_viewer.save()
		activity_domain = {'user_id': domain["user_id"],
		                   'message': ActivityMessage.READ_LESSON,
		                   'raw': json.dumps(lesson.to_dict(), indent=4, sort_keys=True, cls=TeachmeJsonEncoder),
		                   'doc_type': ActivityType.LESSON_VIEWER}
		activity_service.add_activity(activity_domain)
		return {'payload': lesson_viewer.to_dict()}
		
	@Number(['lesson_id'])
	def count_lesson_viewer_by_lesson_id(self, domain):
		count_lesson_viewer = LessonViewer.query.filter_by(lesson_id=domain['lesson_id']).count()
		return {'payload': {'count': count_lesson_viewer}}