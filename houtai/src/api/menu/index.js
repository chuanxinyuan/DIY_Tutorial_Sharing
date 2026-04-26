import request from '@/utils/request';

export function useMenuApi() {
	return {
		getMenuGuanliyuan: (params) => {
			// 本地数据，路径：`/public/xxx.json`
			return request({
				url: './guanliyuan.json',
				method: 'get',
				params,
			});

		},
		getMenuYonghu: (params) => {
			return request({
				url: './yonghu.json',
				method: 'get',
				params,
			});
		},
		
	};
}
