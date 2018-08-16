import re
from slerp.logger import logging
from slerp.validator import Key, Blank, Number
from entity.models import Task, TaskQuestion, TaskAnswer, TaskGroup, UserPrincipal, t_learning_group_user
from constant.api_constant import UserType
from sqlalchemy import and_
log = logging.getLogger(__name__)


class TaskService(object):
	def __init__(self):
		super(TaskService, self).__init__()
	
	@Key(['title', 'user_id'])
	def add_task(self, domain):
		if 'active' not in domain:
			domain['active'] = False
		task_data = {
			'title': domain['title'],
			'active': domain['active'],
			'user_id': domain['user_id']
		}
		if 'time_limit' in domain:
			task_data['time_limit'] = re.sub('[^0-9]', '', str(domain['time_limit']).lower())
		
		task = Task(task_data)
		task.save()
		questions = domain['questions']
		for question in questions:
			question_data = {
				'question': question['question'],
				'question_type': question['type']
			}
			if 'answer_key' in question:
				question_data['answer_key'] = question['answer_key']
			question_data['task_id'] = task.id
			task_question = TaskQuestion(question_data)
			task_question.save()
			if 'answers' in question:
				answers = question['answers']
				for answer in answers:
					answer_data = {
						'answer': answer,
						'question_id': task_question.id
					}
					task_answer = TaskAnswer(answer_data)
					task_answer.save()
					pass
			pass
		# task.save()
		return {'payload': task.to_dict()}
	
	@Blank(['user_id', 'active'])
	@Number(['page', 'size'])
	def get_task_by_user_id(self, domain):
		page = int(domain['page'])
		size = int(domain['size'])
		task_q = Task.query.filter_by(user_id=domain['user_id'], active=domain['active']).order_by(
			Task.created_at.desc()).paginate(page, size, error_out=False)
		task_list = list(map(lambda x: self.handle_task_detail(x.to_dict()), task_q.items))
		return {'payload': task_list, 'total': task_q.total, 'total_pages': task_q.pages}
	
	@staticmethod
	def handle_task_detail(task):
		if task['active']:
			count_user = UserPrincipal.query \
				.join(t_learning_group_user, t_learning_group_user.c.user_id == UserPrincipal.id) \
				.join(TaskGroup, TaskGroup.group_id == t_learning_group_user.c.group_id) \
				.join(Task, TaskGroup.task_id == Task.id) \
				.filter(Task.id == task['id']) \
				.filter(Task.active == task['active']) \
				.filter(UserPrincipal.register_type == UserType.STUDENT)\
				.count()
			task['total_student'] = count_user
		return task
			
	@Number(['page', 'size', 'user_id'])
	def get_task_by_user_group(self, domain):
		page = int(domain['page'])
		size = int(domain['size'])
		task_q = Task.query.join(TaskGroup, Task.id == TaskGroup.task_id) \
			.join(t_learning_group_user, TaskGroup.group_id == t_learning_group_user.c.group_id) \
			.join(UserPrincipal, and_(t_learning_group_user.c.user_id == UserPrincipal.id,
		                              UserPrincipal.id == domain['user_id'])) \
			.filter(UserPrincipal.register_type == UserType.STUDENT) \
			.paginate(page, size, error_out=False)
		task_list = list(map(lambda x: x.to_dict(), task_q.items))
		return {'payload': task_list, 'total': task_q.total, 'total_pages': task_q.pages}

	@Number(['task_id'])
	def get_task_question(self, domain):
		question_q = TaskQuestion.query.filter(TaskQuestion.task_id == domain['task_id']).all()		
		qustion_list = list(map(lambda x: x.to_dict(), question_q))
		return {'payload': qustion_list}
