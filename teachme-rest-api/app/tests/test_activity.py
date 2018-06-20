import unittest

import requests
from slerp.logger import logging

log = logging.getLogger(__name__)


class TestActivity(unittest.TestCase):
	BASE_URL = "http://localhost:5002/"

	def setUp(self):
		pass

	def test_get_activity_by_user_id(self):
		result = requests.get(self.BASE_URL + 'get_activity_by_user_id', params={
		    'size': 10,
		    'page': 1,
		    'user_id': 1
		})
		log.info('test_get_activity_by_user_id: %s', result.json())
		self.assertIs(result.status_code, 200)
		self.assertEqual(result.json()['status'], 'OK')
		

if __name__ == '__main__':
	unittest.main()
