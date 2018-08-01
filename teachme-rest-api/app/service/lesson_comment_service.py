from slerp.logger import logging
from slerp.validator import Number, Blank

from entity.models import LessonComment

log = logging.getLogger(__name__)


class MaterialCommentService(object):
	def __init__(self):
		super(MaterialCommentService, self).__init__()
	
	@Blank(['sender_user_id'])
	@Number(['page', 'size'])
	def get_lesson_comment_by_sender_user_id(self, domain):
	
		page = int(domain['page'])
		size = int(domain['size'])
		lesson_comment_q = LessonComment.query.filter_by(sender_user_id=domain['sender_user_id']).order_by(LessonComment.id.asc()).paginate(page, size, error_out=False)
		lesson_comment_list = list(map(lambda x: x.to_dict(), lesson_comment_q.items))
		return {'payload': lesson_comment_list, 'total': lesson_comment_q.total, 'total_pages': lesson_comment_q.pages}
	
	@Blank(['lesson_id'])
	@Number(['page', 'size'])
	def get_lesson_comment_by_lesson_id(self, domain):
	
		page = int(domain['page'])
		size = int(domain['size'])
		lesson_comment_q = LessonComment.query.filter_by(lesson_id=domain['lesson_id']).order_by(LessonComment.id.desc()).paginate(page, size, error_out=False)
		lesson_comment_list = list(map(lambda x: x.to_dict(), lesson_comment_q.items))
		return {'payload': lesson_comment_list, 'total': lesson_comment_q.total, 'total_pages': lesson_comment_q.pages}