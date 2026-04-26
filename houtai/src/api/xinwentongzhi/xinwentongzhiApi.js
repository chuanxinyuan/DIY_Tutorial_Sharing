import request from '@/utils/request';
// list
export const xinwentongzhiList = (params) => {
    return request({
        url: '/xinwentongzhi/page',
        method: 'post',
        data: params,
    })
};
export const xinwentongzhiDetail = (id) => {
    return request({
        url: '/xinwentongzhi/'+id,
        method: 'get',
    })
};
// add
export const xinwentongzhiSave = (params) => {
    return request({
        url: '/xinwentongzhi',
        method: 'post',
        data: params,
    })
};
// updt
export const xinwentongzhiEdit = (params) => {
    return request({
        url: '/xinwentongzhi',
        method: 'put',
        data: params,
    })
};
// delete
export const xinwentongzhiDelete = (id) => {
    return request({
        url: '/xinwentongzhi/'+id,
        method: 'delete',
    })
};
// pldel
export const xinwentongzhiDeleteList = (xinwentongzhis) => {
    return request({
        url: '/xinwentongzhi/deleteList',
        data:{list:xinwentongzhis},
        method: 'post',
    })
};
// all
export const xinwentongzhiAllList = () => {
    return request({
        url: '/xinwentongzhi',
        method: 'get',
    })
};

