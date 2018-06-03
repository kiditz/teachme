import os

from flask import send_file
from slerp.logger import logging
from slerp.validator import Number, Blank

from entity.models import Document

log = logging.getLogger(__name__)


class DocumentService(object):
	def __init__(self):
		super(DocumentService, self).__init__()
	
	@Blank(['filename', 'mimetype', 'folder', 'original_filename'])
	def add_document(self, domain):
		domain['secure'] = True if domain['secure'] == 'Y' else False
		document = Document(domain)
		document.save()
		return {'payload': document.to_dict()}
	
	@Number(['id'])
	def get_document(self, domain):
		document = Document.query.filter_by(id=domain['id']).first()
		filename = os.path.join(document.folder,
		                        document.filename if 'thumbnails' not in domain else document.thumbnails)
		return send_file(filename, mimetype=document.mimetype if 'thumbnails' not in domain else 'image/png')
	
	@Number(['id'])
	def find_document_by_id(self, domain):
		document = Document.query.filter_by(id=domain['id']).first()
		return {'payload': document.to_dict()}
