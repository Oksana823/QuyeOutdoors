(function(){
  axios.defaults.baseURL='/api';axios.defaults.timeout=5000;
  axios.interceptors.request.use(function(config){var token=sessionStorage.getItem('token');if(token){config.headers.authorization=token}return config});
  axios.interceptors.response.use(function(response){var body=response.data;if(body&&body.success===false){return Promise.reject(new Error(body.errorMsg||'请求失败'))}return body&&Object.prototype.hasOwnProperty.call(body,'data')?body.data:body},function(error){if(error.response&&error.response.status===401){sessionStorage.removeItem('token');location.href='/login.html'}return Promise.reject(error)});
  window.API={
    categories:function(){return axios.get('/place-categories/list')},
    places:function(params){return axios.get('/places/of/type',{params:params})},
    searchPlaces:function(name){return axios.get('/places/of/name',{params:{name:name}})},
    place:function(id){return axios.get('/places/'+id)},
    hotNotes:function(current){return axios.get('/notes/hot',{params:{current:current||1}})},
    note:function(id){return axios.get('/notes/'+id)},
    likeNote:function(id){return axios.put('/notes/like/'+id)},
    noteLikes:function(id){return axios.get('/notes/likes/'+id)},
    myNotes:function(current){return axios.get('/notes/of/me',{params:{current:current||1}})},
    userNotes:function(id,current){return axios.get('/notes/of/users',{params:{id:id,current:current||1}})},
    feed:function(lastId,offset){return axios.get('/notes/of/follows',{params:{lastId:lastId,offset:offset||0}})},
    publish:function(note){return axios.post('/notes',note)},
    upload:function(form){return axios.post('/uploads/notes',form,{headers:{'Content-Type':'multipart/form-data'},timeout:30000})},
    deleteUpload:function(name){return axios.get('/uploads/notes/delete',{params:{name:name}})},
    passes:function(placeId){return axios.get('/passes/list/'+placeId)},
    claimPass:function(id){return axios.post('/pass-orders/flash/'+id)},
    sendCode:function(phone){return axios.post('/users/code?phone='+encodeURIComponent(phone))},
    login:function(form){return axios.post('/users/login',form)},
    logout:function(){return axios.post('/users/logout')},
    me:function(){return axios.get('/users/me')},user:function(id){return axios.get('/users/'+id)},profile:function(id){return axios.get('/users/info/'+id)},updateProfile:function(profile){return axios.put('/users/info',profile)},
    followStatus:function(id){return axios.get('/follows/or/not/'+id)},follow:function(id,value){return axios.put('/follows/'+id+'/'+value)},commonFollows:function(id){return axios.get('/follows/common/'+id)},
    sign:function(){return axios.post('/users/sign')},signCount:function(){return axios.get('/users/sign/count')}
  };
  window.Quye={query:function(name){return new URLSearchParams(location.search).get(name)},images:function(value){return value?value.split(',').filter(Boolean):[]},image:function(value){var p=(window.Quye.images(value)[0]||'imgs/covers/forest.jpg');if(/^\/journeys\//.test(p)){p='/imgs'+p}return /^https?:|^data:|^\//.test(p)?p:'/'+p.replace(/^\.\//,'')},avatar:function(value){return value?window.Quye.image(value):'/imgs/icons/black.jpg'},message:function(vm,text){vm.toast=text;setTimeout(function(){vm.toast=''},2200)},error:function(vm,error){window.Quye.message(vm,error&&error.message?error.message:'操作失败')},requireLogin:function(){if(!sessionStorage.getItem('token')){location.href='/login.html?redirect='+encodeURIComponent(location.pathname+location.search);return false}return true}};
})();
