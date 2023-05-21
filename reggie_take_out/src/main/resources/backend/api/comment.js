//查询列表接口
const getMessagePage = (params) => {
    return $axios({
        url:'/commment/page',
        method:'get',
        params
    })
}
//增加接口
const addMessage =(params)=>{
    return $axios({
        url: '/comment',
        method: 'put',
        data: {...params}
    })
}
//删除接口
const deleteMessage = (id) => {
  return $axios({
      url: `/comment/${id}`,
      method: 'delete'
  })
}
//修改接口
const editMessage =(id) =>{
    return $axios({
        url:`/comment/${id}`,
        method: 'post'
    })
}
//查看单个接口
const queryMessageById = (id) => {
  return $axios({
      url: `/comment/${id}`,
      method: 'get'
  })
}