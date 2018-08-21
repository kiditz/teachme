from flask import Blueprint, request
from slerp.logger import logging
from slerp import sender
from service.task_service import TaskService

log = logging.getLogger(__name__)

task_api_blue_print = Blueprint('task_api_blue_print', __name__)
api = task_api_blue_print
task_service = TaskService()


@api.route('/add_task', methods=['POST'])
def add_task():
    """
    {
    "title": "String",
    "user_id": "Long",
    "active": "Boolean"
    }
    """
    domain = request.get_json()
    return task_service.add_task(domain)


@api.route('/add_task_answer', methods=['POST'])
def add_task_answer():
    """
    {
            "task_id": "Long",
            "user_id": "Long",
            "answers": [
                    {
                            "answer": "String",
                            "question_id": "Long"
                    }
            ]
    }
    """
    domain = request.get_json()
    return task_service.add_task_answer(domain)


@api.route('/get_task_by_user_id', methods=['GET'])
def get_task_by_user_id():
    """
    {
            "page": "Long",
            "size": "Long",
            "user_id": "Long"
    }
    """
    domain = request.args.to_dict()
    return task_service.get_task_by_user_id(domain)


@api.route('/get_task_by_user_group', methods=['GET'])
def get_task_by_user_group():
    """
    {
            "page": "Long",
            "size": "Long",
            "user_id": "Long"
    }
    """
    domain = request.args.to_dict()
    return task_service.get_task_by_user_group(domain)


@api.route('/get_user_in_task', methods=['GET'])
def get_user_in_task():
    """
    {
            "page": "Long",
            "size": "Long",
            "user_id": "Long"
    }
    """
    domain = request.args.to_dict()
    return task_service.get_user_in_task(domain)


@api.route('/get_task_question', methods=['GET'])
def get_task_question():
    """
    {
            "task_id": "Long"
    }
    """
    domain = request.args.to_dict()
    return task_service.get_task_question(domain)


@api.route('/get_question_for_judgement')
def get_question_for_judgement():
    """
    {
            "task_id": "Long"
    }
    """
    domain = request.args.to_dict()
    return task_service.get_question_for_judgement(domain)


@api.route('/get_task_for_edit')
def get_task_for_edit():
    """
    {
            "task_id": "Long"
    }
    """
    domain = request.args.to_dict()
    return task_service.get_task_for_edit(domain)


@api.route('/edit_task_by_id', methods=['PUT'])
def edit_task_by_id():
    """
    {
    "id": "Long",
    "title": "String",
    "user_id": "Long",
    "active": "Boolean",
    "time_limit": "Long"
    }
    """
    domain = request.get_json()
    return task_service.edit_task_by_id(domain)


@api.route('/add_task_score', methods=['POST'])
def add_task_score():
    """
    {
    "task_id": "Long",
    "user_id": "Long",
    "score": "Double"
    }
    """
    domain = request.get_json()
    return task_service.add_task_score(domain)


@api.route('/edit_question_score', methods=['PUT'])
def edit_question_score():
    """
    {
        "question_scores": [
            {
                "id": "String",
                "score": 0
            },
            {
                "id": "String",
                "score": 0
            },
        ]
    }
    """
    domain = request.get_json()
    return task_service.edit_question_score(domain)
