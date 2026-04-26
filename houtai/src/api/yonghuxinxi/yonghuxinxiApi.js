import request from '@/utils/request';
// list
export const yonghuxinxiList = (params) => {
    return request({
        url: '/yonghuxinxi/page',
        method: 'post',
        data: params,
    })
};
export const yonghuxinxiDetail = (id) => {
    return request({
        url: '/yonghuxinxi/'+id,
        method: 'get',
    })
};
// add
export const yonghuxinxiSave = (params) => {
    return request({
        url: '/yonghuxinxi',
        method: 'post',
        data: params,
    })
};
// updt
export const yonghuxinxiEdit = (params) => {
    return request({
        url: '/yonghuxinxi',
        method: 'put',
        data: params,
    })
};
// delete
export const yonghuxinxiDelete = (id) => {
    return request({
        url: '/yonghuxinxi/'+id,
        method: 'delete',
    })
};
// pldel
export const yonghuxinxiDeleteList = (yonghuxinxis) => {
    return request({
        url: '/yonghuxinxi/deleteList',
        data:{list:yonghuxinxis},
        method: 'post',
    })
};
// all
export const yonghuxinxiAllList = () => {
    return request({
        url: '/yonghuxinxi',
        method: 'get',
    })
};
export const yonghuxinxiUpdatePassword = (params) =>{ return request({ url: `/yonghuxinxi/updatePassword`, method: 'put',data: {...params,mima:params.password}, })};
