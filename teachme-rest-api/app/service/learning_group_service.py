from slerp.validator import Key, Number, Blank
from slerp.logger import logging
from slerp.app import db
from sqlalchemy import select, func, and_
from entity.models import LearningGroup, UserPrincipal, t_learning_group_user


log = logging.getLogger(__name__)


class LearningGroupService(object):
	def __init__(self):
		super(LearningGroupService, self).__init__()

	@Key(['name', 'user_id'])
	def add_learning_group(self, domain):		
		learning_group_data = {
			'user_id': domain['user_id'],
			'name': domain['name']			
		}
		if 'document_id' in domain:
			learning_group_data['document_id'] = domain['document_id']

		learning_group = LearningGroup(learning_group_data)
		learning_group.save()		
		gropu_list = []
		users = domain['users']				
		for user in users:
			user['group_id'] = learning_group.id		
			gropu_list.append(user)		
		db.session.execute(t_learning_group_user.insert().values(gropu_list))
		db.session.flush()
		return {'payload': users}
	
	@Blank(['user_id'])
	@Number(['page', 'size'])
	def get_learning_group_by_user_id(self, domain):
		page = int(domain['page'])
		size = int(domain['size'])
		name = ''
		if 'name' in domain:
			name = domain['name']

		learning_group_q = LearningGroup.query \
							.with_entities(LearningGroup.id, 
										   LearningGroup.name, 
										   UserPrincipal.fullname, 
										   LearningGroup.document_id,
										   LearningGroup.user_id, 
										   LearningGroup.created_at) \
							.join(UserPrincipal, UserPrincipal.id == LearningGroup.user_id) \
							.filter(LearningGroup.user_id==domain['user_id']) \
							.filter(LearningGroup.name.ilike('%'+ name +'%')) \
							.order_by(LearningGroup.id.desc()).paginate(page, size, error_out=False)
		group_list = []
		for group in learning_group_q.items:
			group_data = group._asdict()
			count_user = db.session.execute(select([func.count().label('total')]).select_from(t_learning_group_user.join(UserPrincipal, t_learning_group_user.c.user_id == UserPrincipal.id)).where(and_(t_learning_group_user.c.group_id==group.id, UserPrincipal.register_type == 'TEACHER')))			
			for row in count_user:				
				row = dict(row)
				group_data['member'] = row['total']
			group_list.append(group_data)
			pass
		#learning_group_list = list(map(lambda x: x.to_dict(), learning_group_q.items))
		return {'payload': group_list, 'total': learning_group_q.total, 'total_pages': learning_group_q.pages}

	@Number(['user_id'])
	def is_learning_grop_exists_by_user_id(self, domain):
		count = LearningGroup.query.filter_by(user_id=domain['user_id']).count()
		return {'payload': {'exists': count > 0}}