
function  updateUserApi(data){
    return $axios({
        'url': '/user',
        'method': 'put',
        data
    })
}

function userFindOneApi(id) {
    return $axios({
        'url': `/user/${id}`,
        'method': 'get',
    })
}

