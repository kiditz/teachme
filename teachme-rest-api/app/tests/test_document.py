import unittest
import requests
from slerp.logger import logging

log = logging.getLogger(__name__)


class TestDocument(unittest.TestCase):
	BASE_URL = "http://localhost:5002/"

	def setUp(self):
		pass

	def test_get_document(self):
		# TODO : Replace All Input GET
		result = requests.get(self.BASE_URL + 'get_document', params={
		    'id': '4'
		})
		# log.info('test_get_document: %s', result.json())
		self.assertIs(result.status_code, 200)
		# self.assertEqual(result.json()['status'], 'OK')

	def test_find_document_by_id(self):
		# TODO : Replace All Input GET
		result = requests.get(self.BASE_URL + 'find_document_by_id', params={
		    'id': '4'
		})
		log.info('test_find_document_by_id: %s', result.json())
		self.assertIs(result.status_code, 200)
		self.assertEqual(result.json()['status'], 'OK')
		

if __name__ == '__main__':
	unittest.main()
