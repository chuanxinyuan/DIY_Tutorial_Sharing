import request from '@/utils/request';
// list
export const bianhuantuList = (params) => {
    return request({
        url: '/bianhuantu/page',
        method: 'post',
        data: params,
    })
};
export const bianhuantuDetail = (id) => {
    return request({
        url: '/bianhuantu/'+id,
        method: 'get',
    })
};
// add
export const bianhuantuSave = (params) => {
    return request({
        url: '/bianhuantu',
        method: 'post',
        data: params,
    })
};
// updt
export const bianhuantuEdit = (params) => {
    return request({
        url: '/bianhuantu',
        method: 'put',
        data: params,
    })
};
// delete
export const bianhuantuDelete = (id) => {
    return request({
        url: '/bianhuantu/'+id,
        method: 'delete',
    })
};
// pldel
export const bianhuantuDeleteList = (bianhuantus) => {
    return request({
        url: '/bianhuantu/deleteList',
        data:{list:bianhuantus},
        method: 'post',
    })
};
// all
export const bianhuantuAllList = () => {
    return request({
        url: '/bianhuantu',
        method: 'get',
    })
};

