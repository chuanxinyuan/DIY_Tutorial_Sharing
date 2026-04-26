import request from '@/utils/request';
// list
export const ershouwupinList = (params) => {
    return request({
        url: '/ershouwupin/page',
        method: 'post',
        data: params,
    })
};
export const ershouwupinDetail = (id) => {
    return request({
        url: '/ershouwupin/'+id,
        method: 'get',
    })
};
// add
export const ershouwupinSave = (params) => {
    return request({
        url: '/ershouwupin',
        method: 'post',
        data: params,
    })
};
// updt
export const ershouwupinEdit = (params) => {
    return request({
        url: '/ershouwupin',
        method: 'put',
        data: params,
    })
};
// delete
export const ershouwupinDelete = (id) => {
    return request({
        url: '/ershouwupin/'+id,
        method: 'delete',
    })
};
// pldel
export const ershouwupinDeleteList = (ershouwupins) => {
    return request({
        url: '/ershouwupin/deleteList',
        data:{list:ershouwupins},
        method: 'post',
    })
};
// all
export const ershouwupinAllList = () => {
    return request({
        url: '/ershouwupin',
        method: 'get',
    })
};

export const ershouwupin_tj_wupinleixing = () => {return request({url: '/ershouwupin/ershouwupin_tj_wupinleixing',method: 'get',})};