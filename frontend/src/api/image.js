import axios from 'axios'

const API_URL = 'http://localhost:8080/api/images'

export const getImages = () => axios.get(API_URL)

export const uploadImage = (file) => {
    const formData = new FormData()
    formData.append('file', file)
    return axios.post(`${API_URL}/upload`, formData, {
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    })
}

export const deleteImage = (id) => axios.delete(`${API_URL}/${id}`)
