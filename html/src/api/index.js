import http from '../utils/request'


export const bookList=()=>{
    return http.get("/book/list");
}