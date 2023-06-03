//查询列表接口
const getMessagePage = (params) => {
    return $axios({
        url:'/comment/page',
        method:'get',
        params
    })
}
//增加接口
const addMessage =(params) => {
    return $axios({
        url: '/comment',
        method: 'post',
        data: {...params}
    })
}
//删除接口
const deleteMessage = (id) =>{
  return $axios({
      url: '/comment',
      method: 'delete',
      params: { id }
  })
}
//修改接口
const editMessage =(params) =>{
    return $axios({
        url:`/comment`,
        method: 'put',
        data: { ...params}
    })
}
//查看单个接口
const queryMessageById = (id) => {
  return $axios({
      url: `/comment/${id}`,
      method: 'get'
  })
}