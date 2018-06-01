import unittest

import requests
from slerp.logger import logging

log = logging.getLogger(__name__)


class TestSchoolClass(unittest.TestCase):
	BASE_URL = "http://localhost:5002/"
	
	def setUp(self):
		pass
	
	def test_get_school_class_by_level_id(self):
		result = requests.get(self.BASE_URL + 'get_school_class_by_level_id', params={
			'level_id': 4
		})
		log.info('test_get_school_class_by_level_id: %s', result.json())
		self.assertIs(result.status_code, 200)
		self.assertEqual(result.json()['status'], 'OK')
		self.assertIsNotNone(result.json()['payload'])


if __name__ == '__main__':
	unittest.main()
