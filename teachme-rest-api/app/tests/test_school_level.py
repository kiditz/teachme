import unittest
import requests
from slerp.logger import logging

log = logging.getLogger(__name__)


class TestSchoolLevel(unittest.TestCase):
	BASE_URL = "http://localhost:5002/"
	
	def setUp(self):
		pass
	
	def test_get_school_level(self):
		# TODO : Replace All Input GET
		result = requests.get(self.BASE_URL + 'get_school_level', params={
		})
		log.info('test_get_school_level: %s', result.json())
		self.assertIs(result.status_code, 200)
		self.assertEqual(result.json()['status'], 'OK')


if __name__ == '__main__':
	unittest.main()
