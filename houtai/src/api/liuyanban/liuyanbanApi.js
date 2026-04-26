import request from '@/utils/request';
// list
export const liuyanbanList = (params) => {
    return request({
        url: '/liuyanban/page',
        method: 'post',
        data: params,
    })
};
export const liuyanbanDetail = (id) => {
    return request({
        url: '/liuyanban/'+id,
        method: 'get',
    })
};
// add
export const liuyanbanSave = (params) => {
    return request({
        url: '/liuyanban',
        method: 'post',
        data: params,
    })
};
// updt
export const liuyanbanEdit = (params) => {
    return request({
        url: '/liuyanban',
        method: 'put',
        data: params,
    })
};
// delete
export const liuyanbanDelete = (id) => {
    return request({
        url: '/liuyanban/'+id,
        method: 'delete',
    })
};
// pldel
export const liuyanbanDeleteList = (liuyanbans) => {
    return request({
        url: '/liuyanban/deleteList',
        data:{list:liuyanbans},
        method: 'post',
    })
};
// all
export const liuyanbanAllList = () => {
    return request({
        url: '/liuyanban',
        method: 'get',
    })
};
