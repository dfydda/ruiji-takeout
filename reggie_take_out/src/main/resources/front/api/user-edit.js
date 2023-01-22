
function  addUserApi(data){
    return $axios({
        'url': '/user',
        'method': 'post',
        data
    })
}


function  updateUserApi(data){
    return $axios({
        'url': '/user',
        'method': 'put',
        data
    })
}


function deleteUserApi(params) {
    return $axios({
        'url': '/user',
        'method': 'delete',
        params
    })
}


function userFindOneApi(id) {
    return $axios({
        'url': `/user/${id}`,
        'method': 'get',
    })
}

