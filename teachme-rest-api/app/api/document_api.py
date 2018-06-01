import os
from flask import Blueprint, request
from slerp.logger import logging
from slerp.app import app
from slerp.exception import ValidationException, CoreException
from service.document_service import DocumentService
from werkzeug.utils import secure_filename
from constant.api_constant import allowed_image, UPLOAD_FAIL
from utils.thumbsnail import video_thumbnails
from datetime import datetime

log = logging.getLogger(__name__)

document_api_blue_print = Blueprint('document_api_blue_print', __name__)
api = document_api_blue_print
document_service = DocumentService()


@api.route('/add_document', methods=['POST'])
def add_document():
	if 'file' not in request.files:
		raise ValidationException('file.not.found')
	file = request.files['file']
	log.info("filename : %s", file.filename)
	if file.filename == '':
		raise ValidationException('file.cannot.be.empty')
	
	upload_folder = app.config['UPLOAD_FOLDER']
	data = request.form.to_dict()
	user_dir = '' if 'directory' not in data else data['directory']
	log.info('user_dir : %s', user_dir)
	
	directory = os.path.join(os.path.dirname(upload_folder), user_dir)
	if not os.path.exists(directory):
		os.makedirs(directory)
	
	if file and allowed_image(file.filename):
		# setting file upload
		mimetype = file.content_type
		file_time = datetime.now().strftime('%Y_%m_%d_%H_%M_%S')
		original_filename = secure_filename(file.filename)
		filename = file_time + '_' + original_filename
		domain = {'filename': filename, 'folder': directory, 'mimetype': mimetype,
		          'original_filename': original_filename}
		final_file_path = os.path.join(directory, filename)
		file.save(final_file_path)
		if mimetype.startswith('video'):
			domain['thumbnails'] = video_thumbnails(final_file_path)
		return document_service.add_document(domain)
	raise CoreException(UPLOAD_FAIL)


@api.route('/get_document', methods=['GET'])
def get_document():
	"""
    {
        "id": "Long"
    }
    """
	domain = request.args.to_dict()
	return document_service.get_document(domain)