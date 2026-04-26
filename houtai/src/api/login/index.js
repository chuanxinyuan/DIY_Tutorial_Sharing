import request from '@/utils/request';

export const login =(params) => {
	if(params.role == '管理员'){return request({url: '/guanliyuan/login',method: 'post',data: {yonghuming:params.username,mima:params.password},});}
	else if(params.role == '用户'){return request({url: '/yonghuxinxi/login',method: 'post',data: {yonghuming:params.username,mima:params.password},});}
	
	
}
export const register =(params) => {return request({url: '/yonghuxinxi/register',method: 'post',data: {yonghuming:params.username,mima:params.password},})}