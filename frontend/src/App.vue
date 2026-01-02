<template>
  <div class="gallery-container">
    <div class="header">
      <h1>☁️ My Cute Gallery ☁️</h1>
      <el-upload
        class="upload-demo"
        action="#"
        :http-request="handleUpload"
        :show-file-list="false"
      >
        <el-button color="#F8BBD0" class="upload-btn" :icon="Plus" circle size="large"></el-button>
      </el-upload>
    </div>

    <div class="image-grid">
      <el-empty v-if="images.length === 0" description="No images yet! Upload some cuteness!" />
      <div v-for="img in images" :key="img.id" class="image-card">
        <el-image 
          :src="img.url" 
          fit="cover" 
          class="gallery-image"
          :preview-src-list="[img.url]"
          hide-on-click-modal
        />
        <div class="image-actions">
           <el-button type="danger" :icon="Delete" circle size="small" @click="handleDelete(img.id)" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Plus, Delete } from '@element-plus/icons-vue'
import { getImages, uploadImage, deleteImage } from './api/image'
import { ElMessage } from 'element-plus'

const images = ref([])

const loadImages = async () => {
  try {
    const res = await getImages()
    images.value = res.data
  } catch (err) {
    ElMessage.error('Failed to load images')
  }
}

const handleUpload = async (options) => {
  try {
    await uploadImage(options.file)
    ElMessage.success('Uploaded successfully!')
    loadImages()
  } catch (err) {
    ElMessage.error('Upload failed')
  }
}

const handleDelete = async (id) => {
  try {
    await deleteImage(id)
    ElMessage.success('Deleted successfully')
    loadImages()
  } catch (err) {
    ElMessage.error('Delete failed')
  }
}

onMounted(() => {
  loadImages()
})
</script>

<style>
@import url('https://fonts.googleapis.com/css2?family=Varela+Round&display=swap');

body {
  margin: 0;
  background-color: #FFF0F5; /* Lavender Blush */
  font-family: 'Varela Round', sans-serif;
}

.gallery-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.header {
  text-align: center;
  margin-bottom: 40px;
  color: #880E4F;
}

.header h1 {
  font-size: 2.5rem;
  text-shadow: 2px 2px #FCE4EC;
  margin-bottom: 20px;
}

.upload-btn {
  font-size: 1.5rem;
  transition: transform 0.2s;
}

.upload-btn:hover {
  transform: scale(1.1);
}

.image-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px;
}

.image-card {
  position: relative;
  border-radius: 15px;
  overflow: hidden;
  box-shadow: 0 4px 15px rgba(248, 187, 208, 0.4);
  transition: transform 0.3s;
  background: white;
  aspect-ratio: 1;
}

.image-card:hover {
  transform: translateY(-5px);
}

.gallery-image {
  width: 100%;
  height: 100%;
  border-radius: 15px;
}

.image-actions {
  position: absolute;
  top: 10px;
  right: 10px;
  opacity: 0;
  transition: opacity 0.3s;
}

.image-card:hover .image-actions {
  opacity: 1;
}
</style>
