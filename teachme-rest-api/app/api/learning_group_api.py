from flask import Blueprint, request
from slerp.logger import logging

from service.learning_group_service import LearningGroupService

log = logging.getLogger(__name__)

learning_group_api_blue_print = Blueprint('learning_group_api_blue_print', __name__)
api = learning_group_api_blue_print
learning_group_service = LearningGroupService()


@api.route('/add_learning_group', methods=['POST'])
def add_learning_group():
    """
    {
    "name": "String",
    "user_id": "Long"
    }
    """
    domain = request.get_json()
    return learning_group_service.add_learning_group(domain)


@api.route('/get_learning_group_by_user_id', methods=['GET'])
def get_learning_group_by_user_id():

    """
    {
        "page": "Long",
        "size": "Long",
        "user_id": "Long"
    }
    """
    domain = request.args.to_dict()
    return learning_group_service.get_learning_group_by_user_id(domain)

@api.route('/is_learning_grop_exists_by_user_id', methods=['GET'])
def is_learning_grop_exists_by_user_id():

    """
    {
        "user_id": "Long"
    }
    """
    domain = request.args.to_dict()
    return learning_group_service.is_learning_group_exists_by_user_id(domain)