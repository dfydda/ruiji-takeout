//查询接口(列表)
const getTextPage = (params) =>{
    return $axios({
        url: '/forum/page',
        method: 'get',
        params
    })
}
//查询文章信息接口
const queryTextById = (id) => {
  return $axios({
      url: `/forum/${id}`,
      method: 'get',
  })
}
//删除接口
const deleteText = (id) =>{
    return $axios({
        url: '/forum',
        method: 'delete',
        params: { id }
    })
}
//新增接口
const addText = (params) => {
  return $axios({
      url: '/forum',
      method: 'post',
      data: { ...params}
  })
}
//修改接口
const editText = (params) => {
  return $axios({
      url: '/forum',
      method: 'put',
      data: { ...params}
  })
}
