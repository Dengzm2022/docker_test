<template>
	<view class="content">
		<view class="header">
			<text class="title">☁️ My Cute Gallery ☁️</text>
		</view>
        <view class="upload-area" @click="chooseImage">
            <text class="upload-btn">+</text>
        </view>
		<view class="grid">
			<view class="grid-item" v-for="(item, index) in images" :key="index">
				<image :src="item.url" mode="aspectFill" class="image" @click="previewImage(item.url)"></image>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				images: []
			}
		},
		onLoad() {
            this.loadImages();
		},
		methods: {
            loadImages() {
                uni.request({
                    url: 'http://localhost:8080/api/images', 
                    success: (res) => {
                        this.images = res.data;
                    }
                });
            },
            chooseImage() {
                uni.chooseImage({
                    count: 1,
                    success: (res) => {
                        const tempFilePaths = res.tempFilePaths;
                        uni.uploadFile({
                            url: 'http://localhost:8080/api/images/upload',
                            filePath: tempFilePaths[0],
                            name: 'file',
                            success: (uploadFileRes) => {
                                console.log(uploadFileRes.data);
                                this.loadImages();
                            }
                        });
                    }
                });
            },
            previewImage(current) {
                uni.previewImage({
                    urls: this.images.map(img => img.url),
                    current: current
                });
            }
		}
	}
</script>

<style>
	.content {
		display: flex;
		flex-direction: column;
		align-items: center;
		padding: 20px;
        background-color: #FFF0F5;
        min-height: 100vh;
	}
    .header {
        margin-bottom: 20px;
    }
    .title {
        font-size: 24px;
        color: #880E4F;
        font-weight: bold;
    }
    .upload-area {
        width: 60px;
        height: 60px;
        background-color: #F8BBD0;
        border-radius: 30px;
        display: flex;
        justify-content: center;
        align-items: center;
        margin-bottom: 20px;
        box-shadow: 0 4px 10px rgba(0,0,0,0.1);
    }
    .upload-btn {
        font-size: 40px;
        color: white;
        line-height: 50px;
    }
    .grid {
        display: flex;
        flex-wrap: wrap;
        width: 100%;
        justify-content: space-between;
    }
    .grid-item {
        width: 48%;
        height: 150px;
        margin-bottom: 15px;
        border-radius: 10px;
        overflow: hidden;
        box-shadow: 0 2px 5px rgba(0,0,0,0.1);
        background-color: white;
    }
    .image {
        width: 100%;
        height: 100%;
    }
</style>
