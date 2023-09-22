<template>
  <el-table
      :data="tableData"
      border
      style="width: 100%">
    <el-table-column
        fixed
        prop="schoolName"
        label="学校名称"
        width="120">
    </el-table-column>
    <el-table-column
        prop="createTime"
        label="创建时间"
        width="150">
    </el-table-column>

    <el-table-column
        prop="avatarURL"
        label="头像"
        width="120">
      <template slot-scope="scope">
        <img :src="scope.row.avatarURL" alt="avatar" />
      </template>
    </el-table-column>
    <el-table-column
        prop="description"
        label="描述内容"
        width="300">
    </el-table-column>
    <el-table-column
        prop="wechatNumber"
        label="微信号"
        width="120">
    </el-table-column>
    <el-table-column
        prop="phoneNumber"
        label="手机号"
        width="120">
    </el-table-column>
    <el-table-column
        fixed="right"
        label="操作"
        width="200">
      <template slot-scope="scope">
        <el-button @click="handleApprove(scope.row)" type="text" size="small">通过审核</el-button>
        <el-button @click="handleReject(scope.row)" type="text" size="small">不通过审核</el-button>
      </template>
    </el-table-column>
  </el-table>
</template>

<script>
export default {
  data() {
    return {
      tableData: [
        {
          schoolId: 1,
          schoolName: '学校A',
          createTime: '2022-01-01 00:00:00',
          avatarURL: 'http://example.com/avatar.jpg',
          description: '这是学校A的描述',
          wechatNumber: 'wechatA',
          phoneNumber: '12345678901'
        },
        {
          schoolId: 2,
          schoolName: '学校B',
          createTime: '2022-02-01 00:00:00',
          avatarURL: 'http://example.com/avatar2.jpg',
          description: '这是学校B的描述',
          wechatNumber: 'wechatB',
          phoneNumber: '09876543210'
        },
        // 更多数据...
      ]
    }
  },
  methods: {
    handleApprove(row) {
      console.log('Approve', row);
    },
    handleReject(row) {
      console.log('Reject', row);
    }
  }
}
</script>
