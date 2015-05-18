use ishare;

select * from ishare.pool_in_process_order;

select * from ishare.pool_in_process_order_joiner_map;

select * from ishare.pool_joiner;

select joiner.id as joinerId , joiner.seats_count from ishare.pool_joiner joiner, ishare.pool_in_process_order_joiner_map map
	where joiner.id = map.pool_joiner_id and map.pool_in_process_order_id = 1;

select * from system.user;

grant all PRIVILEGES on ishare.* to root@localhost identified by '1234';

select * from ishare.pool_in_process_order;

select * from ishare.pool_subject;

select * from ishare.pool_in_process_order where start_time = '2014-06-23 21:27:41';

select * from ishare.pool_joiner;

select * from ishare.route;

select * from ishare.pool_in_process_order_joiner_map;

select * from ishare.pool_in_process_order_joiner_map where user_id = 1 and pool_in_process_order_id = 1;

update ishare.pool_joiner joiner set joiner.status = "active"
	where joiner.id = (select map.pool_joiner_id from ishare.pool_in_process_order_joiner_map map
		where user_id = 1 and pool_in_process_order_id = 1);

select joiner.status from ishare.pool_in_process_order_joiner_map map, ishare.pool_joiner joiner
	where map.pool_in_process_order_id = 1 and map.pool_joiner_id = joiner.id;

select * from  (
	select o.id, sum(seats_count) as seats, o.total_seats from ishare.pool_in_process_order o, 
		ishare.pool_in_process_order_joiner_map map, pool_joiner joiner
			where o.id = map.pool_in_process_order_id and joiner.id = map.pool_joiner_id 
				and o.id not in (select o.id from ishare.pool_in_process_order o, ishare.pool_in_process_order_joiner_map map
					where map.user_id = 0)
				group by o.id) as orders 
					where orders.seats <= orders.total_seats - 1;

select * from ishare.pool_in_process_order o, 
		ishare.pool_in_process_order_joiner_map map, pool_joiner joiner, pool_subject subject
			where o.id = map.pool_in_process_order_id and joiner.id = map.pool_joiner_id and subject.id = o.pool_subject_id
				group by o.id;

select o.id from ishare.pool_in_process_order o, ishare.pool_in_process_order_joiner_map map
	where map.user_id = 58;

delete from ishare.pool_in_process_order;

select * from ishare.user_baidu_push;
select * from ishare.user_token;
select * from ishare.user;

