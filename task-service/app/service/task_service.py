import re
from slerp.logger import logging
from slerp.validator import Key, Blank, Number
from slerp.sender import send_message
from slerp.exception import ValidationException
from slerp.app import db
from entity.models import QuestionScore, Task, TaskQuestion, TaskAnswer, TaskGroup, UserPrincipal, SchoolLevel, SchoolClass, TaskScore, t_learning_group_user
from constant.api_constant import UserType, ErrorCode
from sqlalchemy import and_

log = logging.getLogger(__name__)


class TaskService(object):
    def __init__(self):
        super(TaskService, self).__init__()

    @Key(['title', 'user_id'])
    def add_task(self, domain):
        log.info('Task Input : %s', domain)
        if 'active' not in domain:
            domain['active'] = False
        task_data = {
            'title': domain['title'],
            'active': domain['active'],
            'user_id': domain['user_id']
        }

        if 'time_limit' in domain:
            task_data['time_limit'] = re.sub(
                '[^0-9]', '', str(domain['time_limit']).lower())

        if 'task_id' in domain:
            task_data['id'] = domain['task_id']
            task = Task.query.filter_by(id=task_data['id']).first()
            task.update(task_data)
        else:
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
            if 'id' in question:
                question_data['id'] = question['id']
                task_question = TaskQuestion.query.filter_by(
                    id=question_data['id']).first()
                task_question.update(question_data)
            else:
                task_question = TaskQuestion(question_data)
                task_question.save()
            if 'answers' in question:
                answers = question['answers']
                for answer in answers:
                    answer_data = {
                        'answer': answer['answer'],
                        'question_id': task_question.id
                    }
                    if 'id' in answer:
                        answer_data['id'] = answer['id']
                        task_answer = TaskAnswer.query.filter_by(
                            id=answer_data['id']).first()
                        task_answer.update(answer_data)
                    else:
                        task_answer = TaskAnswer(answer_data)
                        task_answer.save()
                    pass
            pass
        return {'payload': task.to_dict()}

    @Blank(['answers.answer'])
    @Number(['task_id', 'user_id', 'answers.question_id'])
    def add_task_answer(self, domain):
        send_message('task', domain)
        return {'payload': {'success': True}}

    @Blank(['user_id', 'active'])
    @Number(['page', 'size'])
    def get_task_by_user_id(self, domain):
        page = int(domain['page'])
        size = int(domain['size'])
        task_q = Task.query.filter_by(user_id=domain['user_id'], active=domain['active']).order_by(
            Task.created_at.desc()).paginate(page, size, error_out=False)
        task_list = list(
            map(lambda x: self.handle_task_detail(x.to_dict()), task_q.items))
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
                .filter(UserPrincipal.register_type == UserType.STUDENT) \
                .count()
            task['total_student'] = count_user
        return task

    @Number(['page', 'size', 'user_id'])
    def get_task_by_user_group(self, domain):
        page = int(domain['page'])
        size = int(domain['size'])
        task_q = Task.query.join(TaskGroup, Task.id == TaskGroup.task_id) \
            .join(t_learning_group_user, TaskGroup.group_id == t_learning_group_user.c.group_id) \
            .join(UserPrincipal, and_(t_learning_group_user.c.user_id == UserPrincipal.id, UserPrincipal.id == domain['user_id'])) \
            .filter(UserPrincipal.register_type == UserType.STUDENT) \
            .paginate(page, size, error_out=False)
        task_list = list(map(lambda x: self.check_has_judge(x.to_dict()), task_q.items))
        return {'payload': task_list, 'total': task_q.total, 'total_pages': task_q.pages}
    
    @staticmethod
    def check_has_judge(task):
        task['has_finish'] = db.session.query(QuestionScore.query.filter_by(task_id=task['id']).exists()).scalar()
        return task
        
    @Number(['task_id'])
    def get_task_question(self, domain):
        question_q = TaskQuestion.query.filter(
            TaskQuestion.task_id == domain['task_id']).order_by(TaskQuestion.id.asc()).all()
        qustion_list = list(map(lambda x: x.to_dict(), question_q))
        return {'payload': qustion_list}

    @Number(['page', 'size', 'task_id'])
    def get_user_in_task(self, domain):
        page = int(domain['page'])
        size = int(domain['size'])
        task_q = UserPrincipal.query.with_entities(
            UserPrincipal.id,
            UserPrincipal.phone_number,
            UserPrincipal.username,
            UserPrincipal.fullname,
            UserPrincipal.gender,
            SchoolLevel.name.label('level'),
            SchoolClass.name.label('class_name'),
            UserPrincipal.level_id,
            UserPrincipal.class_id,
            TaskScore.score
        ).join(t_learning_group_user, t_learning_group_user.c.user_id == UserPrincipal.id) \
            .join(SchoolLevel, UserPrincipal.level_id == SchoolLevel.id) \
            .outerjoin(SchoolClass, UserPrincipal.class_id == SchoolClass.id) \
            .join(TaskGroup, t_learning_group_user.c.group_id == TaskGroup.group_id) \
            .join(Task, Task.id == TaskGroup.task_id) \
            .outerjoin(TaskScore, TaskScore.task_id == Task.id) \
            .filter(UserPrincipal.register_type == UserType.STUDENT) \
            .filter(Task.id == domain['task_id']) \
            .order_by(UserPrincipal.fullname.asc()) \
            .paginate(page, size, error_out=False)
        task_list = list(map(lambda x: x._asdict(), task_q.items))
        return {'payload': task_list, 'total': task_q.total, 'total_pages': task_q.pages}

    @Number(['page', 'size', 'user_id'])
    def get_question_for_judgement(self, domain):
        page = int(domain['page'])
        size = int(domain['size'])
        entities = (
            TaskQuestion.task_id,
            TaskQuestion.question,
            QuestionScore.score,
            QuestionScore.user_answer,
            TaskQuestion.answer_key,
            QuestionScore.id.label('score_id')
        )
        question_q = TaskQuestion.query.with_entities(*entities) \
            .join(QuestionScore, QuestionScore.question_id == TaskQuestion.id) \
            .filter(QuestionScore.user_id == domain['user_id']) \
            .order_by(TaskQuestion.id.asc()) \
            .paginate(page, size, error_out=False)
        question_list = list(map(lambda x: x._asdict(), question_q.items))
        return {'payload': question_list, 'total': question_q.total, 'total_pages': question_q.pages}

    @Number(['task_id'])
    def get_task_for_edit(self, domain):
        task = Task.query.filter_by(id=domain['task_id']).first()
        if task == None:
            raise ValidationException(ErrorCode.TASK_NOT_FOUND)
        task_data = {
            'task_id': task.id,
            'active': task.active,
            'user_id': task.user_id,
            'title': task.title,
            'time_limit': task.time_limit
        }
        question_q = TaskQuestion.query.with_entities(TaskQuestion.id, TaskQuestion.answer_key, TaskQuestion.question, TaskQuestion.question_type.label('type')) \
            .filter(TaskQuestion.task_id == task.id) \
            .order_by(TaskQuestion.id.asc())
        question_list = list(
            map(lambda x: self.handle_question(x._asdict()), question_q.all()))
        task_data['questions'] = question_list
        return {'payload': task_data}

    @staticmethod
    def handle_question(question):
        answer_q = TaskAnswer.query.with_entities(
            TaskAnswer.answer, TaskAnswer.id).filter_by(question_id=question['id']).order_by(TaskAnswer.id.asc())
        answer_list = list(map(lambda x: x._asdict(), answer_q.all()))
        question['answers'] = answer_list
        return question

    @Number(['task_id', 'user_id', 'score'])
    def add_task_score(self, domain):
        task_score = TaskScore.query.filter_by(
            task_id=domain['task_id'], user_id=domain['user_id']).first()
        if task_score is not None:
            task_score.update(domain)
        else:
            task_score = TaskScore(domain)
            task_score.save()
        return {'payload': task_score.to_dict()}

    @Number(['question_scores.id', 'question_scores.score'])
    def edit_question_score(self, domain):
        db.session.bulk_update_mappings(
            QuestionScore, domain['question_scores'])
        return {'payload': domain}
