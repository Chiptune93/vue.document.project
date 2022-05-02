<template>
  <div>
    <div class="cont_box" v-if="uploadedFile.length > 0">
      <p v-for="(item, index) in uploadedFile" v-bind:key="index">
        <a
          class="hyper"
          :href="'/file/download.do?seq=' + item.file_seq"
          alt="첨부파일"
          ><span>{{ item.file_name }}</span></a
        >
      </p>
    </div>
    <div class="cont_box">
      <div id="viewer"></div>
    </div>
  </div>
</template>
<script>
module.exports = {
  props: ["callback", "params", "info"],
  emits: ["uploadedFile"],
  data: function () {
    return {
      title: this.info.title,
      contents: this.info.contents,
      regDttm: this.info.reg_dttm,
      regId: this.info.reg_id,
      modDttm: this.info.mod_dttm,
      modId: this.info.mod_id,
      fileMasterSeq: this.info.file_master_seq,
      uploadedFile: [],
      viewer: "",
    };
  },
  mounted: function () {
    this.viewer = Editor.factory({
      el: document.querySelector("#viewer"),
      viewer: true,
      initialValue: this.contents,
      height: "500px",
    });
    if (this.fileMasterSeq > 0) this.getFileList();
  },
  created: function () {},
  distroyed: function () {},
  methods: {
    getFileList: function () {
      let vm = this;
      // 첨부파일 가져오기
      comm.post(
        {
          url: "/data/getFileList",
          params: {
            fileMasterSeq: vm.fileMasterSeq,
          },
        },
        function (data) {
          if (data.result.length < 1) {
            return false;
          } else {
            vm.uploadedFile = data.result;
            vm.$emit("uploadedFile", vm.uploadedFile);
          }
        }
      );
    },
  },
};
</script>
