import request from '@/utils/request';

export const tpAdminUsers = (adminUserId, keyword) => request({
  url: '/front/v2/admin/users',
  method: 'get',
  params: { adminUserId, keyword: keyword || undefined },
});

export const tpAdminToggleUser = (params) => request({
  url: '/front/v2/admin/user/toggle',
  method: 'post',
  data: params,
});

export const tpAdminResetPassword = (params) => request({
  url: '/front/v2/admin/user/reset-password',
  method: 'post',
  data: params,
});

export const tpAdminTutorials = (adminUserId) => request({
  url: '/front/v2/admin/tutorials',
  method: 'get',
  params: { adminUserId },
});

export const tpAdminTutorialDetail = (adminUserId, tutorialId) => request({
  url: `/front/v2/admin/tutorial/${tutorialId}/detail`,
  method: 'get',
  params: { adminUserId },
});

export const tpAdminRemoveTutorial = (params) => request({
  url: '/front/v2/admin/tutorial/remove',
  method: 'post',
  data: params,
});

export const tpAdminTutorialBanPublic = (params) => request({
  url: '/front/v2/admin/tutorial/ban-public',
  method: 'post',
  data: params,
});

export const tpAdminPosts = (adminUserId) => request({
  url: '/front/v2/admin/posts',
  method: 'get',
  params: { adminUserId },
});

export const tpAdminPostSetStatus = (params) => request({
  url: '/front/v2/admin/post/set-status',
  method: 'post',
  data: params,
});

export const tpAdminRemovePost = (params) => request({
  url: '/front/v2/admin/community/remove',
  method: 'post',
  data: params,
});

export const tpAdminPostComments = (adminUserId, postId) => request({
  url: '/front/v2/admin/post-comments',
  method: 'get',
  params: { adminUserId, postId },
});

export const tpAdminPostCommentSetStatus = (params) => request({
  url: '/front/v2/admin/post-comment/set-status',
  method: 'post',
  data: params,
});

export const tpAdminRemovePostComment = (params) => request({
  url: '/front/v2/admin/post-comment/remove',
  method: 'post',
  data: params,
});

export const tpAdminKits = (adminUserId) => request({
  url: '/front/v2/admin/kits',
  method: 'get',
  params: { adminUserId },
});

export const tpAdminOffShelfKit = (params) => request({
  url: '/front/v2/admin/kit/off-shelf',
  method: 'post',
  data: params,
});

export const tpAdminDeleteKit = (params) => request({
  url: '/front/v2/admin/kit/delete',
  method: 'post',
  data: params,
});

export const tpAdminKitSetStatus = (params) => request({
  url: '/front/v2/admin/kit/set-status',
  method: 'post',
  data: params,
});
