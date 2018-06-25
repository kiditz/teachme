from flask import Blueprint, request
from slerp.logger import logging

from service.material_viewer_service import MaterialViewerService

log = logging.getLogger(__name__)

material_viewer_api_blue_print = Blueprint('material_viewer_api_blue_print', __name__)
api = material_viewer_api_blue_print
material_viewer_service = MaterialViewerService()


@api.route('/add_material_viewer', methods=['POST'])
def add_material_viewer():
    """
    {
    "user_id": "Long",
    "material_id": "Long"
    }
    """
    domain = request.get_json()
    return material_viewer_service.add_material_viewer(domain)