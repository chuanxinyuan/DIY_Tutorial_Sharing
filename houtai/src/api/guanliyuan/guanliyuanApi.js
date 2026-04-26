import request from '@/utils/request';
// list
export const guanliyuanList = (params) => {
    return request({
        url: '/guanliyuan/page',
        method: 'post',
        data: params,
    })
};
export const guanliyuanDetail = (id) => {
    return request({
        url: '/guanliyuan/'+id,
        method: 'get',
    })
};
// add
export const guanliyuanSave = (params) => {
    return request({
        url: '/guanliyuan',
        method: 'post',
        data: params,
    })
};
// updt
export const guanliyuanEdit = (params) => {
    return request({
        url: '/guanliyuan',
        method: 'put',
        data: params,
    })
};
// delete
export const guanliyuanDelete = (id) => {
    return request({
        url: '/guanliyuan/'+id,
        method: 'delete',
    })
};
// pldel
export const guanliyuanDeleteList = (guanliyuans) => {
    return request({
        url: '/guanliyuan/deleteList',
        data:{list:guanliyuans},
        method: 'post',
    })
};
// all
export const guanliyuanAllList = () => {
    return request({
        url: '/guanliyuan',
        method: 'get',
    })
};
export const guanliyuanUpdatePassword = (params) =>{ return request({ url: `/guanliyuan/updatePassword`, method: 'put',data: {...params,mima:params.password}, })};