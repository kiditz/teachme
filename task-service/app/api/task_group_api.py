from flask import Blueprint, request
from slerp.logger import logging

from service.task_group_service import TaskGroupService

log = logging.getLogger(__name__)

task_group_api_blue_print = Blueprint('task_group_api_blue_print', __name__)
api = task_group_api_blue_print
task_group_service = TaskGroupService()


@api.route('/add_task_group', methods=['POST'])
def add_task_group():
    """
    {
    "task_id": "Long",
    "group_id": "Long"
    }
    """
    domain = request.get_json()
    return task_group_service.add_task_group(domain)