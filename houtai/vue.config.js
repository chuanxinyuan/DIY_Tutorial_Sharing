module.exports = {
	productionSourceMap: false,
	publicPath: process.env.NODE_ENV === 'production' ? './' : '/',
	lintOnSave: false,
	devServer: {
		open: process.env.VUE_APP_OPEN === 'false' ? false : true, // 自动打开浏览器
		host: '0.0.0.0', // 真机模拟，使用
		port: process.env.VUE_APP_PORT, // 前台代理端口号
		https: false, // https： {type: Booleam}
		hotOnly: false, // 热更新
		proxy: {
			// 设置代理 — 用 bypass 函数过滤 HMR 热更新请求
			'/': {
				target: 'http://localhost:8888/',
				ws: true,
				changeOrigin: true,
				pathRewrite: {
					'^/': '',
				},
				// 跳过 webpack 热更新请求，不让它们被代理到后端
				bypass: function(req) {
					if (req.url.indexOf('.hot-update.') !== -1 || req.url.indexOf('__webpack_hmr') !== -1) {
						return req.url;
					}
				},
			},
		},
	},
	chainWebpack(config) {
		// 移除打包后 index.html 所有打包好的文件都预加载行为
		config.plugins.delete('preload');
		config.plugins.delete('prefetch');
	},
	css: {
		extract: { ignoreOrder: true },
	},
};
