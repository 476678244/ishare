use ishare;

SELECT * FROM ishare.user;

delete from ishare.message where to_user_name = 'zonghan';
delete from ishare.user_baidu_push where user_id = 7;
delete from ishare.user_token where user_id = 7;
delete from ishare.user_route_map where user_id = 7;
delete from ishare.user where id = 7;

SELECT * FROM ishare.user;