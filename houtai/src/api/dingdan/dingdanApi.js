import request from '@/utils/request';
// list
export const dingdanList = (params) => {
    return request({
        url: '/dingdan/page',
        method: 'post',
        data: params,
    })
};
export const dingdanDetail = (id) => {
    return request({
        url: '/dingdan/'+id,
        method: 'get',
    })
};
// add
export const dingdanSave = (params) => {
    return request({
        url: '/dingdan',
        method: 'post',
        data: params,
    })
};
// updt
export const dingdanEdit = (params) => {
    return request({
        url: '/dingdan',
        method: 'put',
        data: params,
    })
};
// delete
export const dingdanDelete = (id) => {
    return request({
        url: '/dingdan/'+id,
        method: 'delete',
    })
};
// pldel
export const dingdanDeleteList = (dingdans) => {
    return request({
        url: '/dingdan/deleteList',
        data:{list:dingdans},
        method: 'post',
    })
};
// all
export const dingdanAllList = () => {
    return request({
        url: '/dingdan',
        method: 'get',
    })
};

export const dingdan_tj_wupinleixing = () => {return request({url: '/dingdan/dingdan_tj_wupinleixing',method: 'get',})};