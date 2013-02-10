using System;
using System.Collections.Generic;
using System.Data.Entity;
using System.Linq;
using System.Web;

namespace ThirdEye.Models
{
    public class FlowerContext : DbContext
    {
        public FlowerContext()
            : base("FlowerConnection")
        {
        }
        public DbSet<Flower> Flowers { get; set; }
        public DbSet<Command> Commands { get; set; }
        public DbSet<UserProfile> UserProfiles { get; set; }
    }
}